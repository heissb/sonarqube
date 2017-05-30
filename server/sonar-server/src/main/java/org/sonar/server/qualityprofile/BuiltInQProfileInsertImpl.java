/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.qualityprofile;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.ActiveRuleParam;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.api.utils.System2;
import org.sonar.core.util.UuidFactory;
import org.sonar.core.util.stream.MoreCollectors;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.qualityprofile.ActiveRuleDto;
import org.sonar.db.qualityprofile.ActiveRuleKey;
import org.sonar.db.qualityprofile.ActiveRuleParamDto;
import org.sonar.db.qualityprofile.DefaultQProfileDto;
import org.sonar.db.qualityprofile.RulesProfileDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.rule.RuleParamDto;
import org.sonar.server.util.TypeValidations;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.requireNonNull;

public class BuiltInQProfileInsertImpl implements BuiltInQProfileInsert {
  private final DbClient dbClient;
  private final System2 system2;
  private final UuidFactory uuidFactory;
  private final TypeValidations typeValidations;
  private RuleRepository ruleRepository;

  public BuiltInQProfileInsertImpl(DbClient dbClient, System2 system2, UuidFactory uuidFactory, TypeValidations typeValidations) {
    this.dbClient = dbClient;
    this.system2 = system2;
    this.uuidFactory = uuidFactory;
    this.typeValidations = typeValidations;
  }

  @Override
  public void create(DbSession session, DbSession batchSession, BuiltInQProfile builtInQProfile, OrganizationDto organization) {
    initRuleRepository(batchSession);

    Date now = new Date(system2.now());
    RulesProfileDto profileDto = insertQualityProfile(session, builtInQProfile, organization, now);

    List<ActiveRuleChange> localChanges = builtInQProfile.getActiveRules()
      .stream()
      .map(activeRule -> insertActiveRule(session, profileDto, activeRule, now.getTime()))
      .collect(MoreCollectors.toList());

    localChanges.forEach(change -> dbClient.qProfileChangeDao().insert(batchSession, change.toDto(null)));
  }

  private void initRuleRepository(DbSession session) {
    if (ruleRepository == null) {
      ruleRepository = new RuleRepository(dbClient, session);
    }
  }

  private RulesProfileDto insertQualityProfile(DbSession dbSession, BuiltInQProfile builtInQProfile, OrganizationDto organization, Date now) {
    RulesProfileDto profileDto = RulesProfileDto.createFor(uuidFactory.create())
      .setName(builtInQProfile.getName())
      .setOrganizationUuid(organization.getUuid())
      .setLanguage(builtInQProfile.getLanguage())
      .setIsBuiltIn(true)
      .setRulesUpdatedAtAsDate(now);
    dbClient.qualityProfileDao().insert(dbSession, profileDto);
    if (builtInQProfile.isDefault()) {
      dbClient.defaultQProfileDao().insertOrUpdate(dbSession, DefaultQProfileDto.from(profileDto));
    }
    return profileDto;
  }

  private ActiveRuleChange insertActiveRule(DbSession session, RulesProfileDto profileDto, org.sonar.api.rules.ActiveRule activeRule, long now) {
    RuleKey ruleKey = RuleKey.of(activeRule.getRepositoryKey(), activeRule.getRuleKey());
    RuleDefinitionDto ruleDefinitionDto = ruleRepository.getDefinition(ruleKey)
      .orElseThrow(() -> new IllegalStateException("RuleDefinition not found for key " + ruleKey));

    ActiveRuleDto dto = ActiveRuleDto.createFor(profileDto, ruleDefinitionDto);
    dto.setSeverity(firstNonNull(activeRule.getSeverity().name(), ruleDefinitionDto.getSeverityString()));
    dto.setUpdatedAt(now);
    dto.setCreatedAt(now);
    dbClient.activeRuleDao().insert(session, dto);

    List<ActiveRuleParamDto> paramDtos = insertActiveRuleParams(session, activeRule, ruleKey, dto);

    ActiveRuleChange change = ActiveRuleChange.createFor(ActiveRuleChange.Type.ACTIVATED, ActiveRuleKey.of(profileDto.getKee(), ruleKey));
    change.setSeverity(dto.getSeverityString());
    paramDtos.forEach(paramDto -> change.setParameter(paramDto.getKey(), paramDto.getValue()));
    return change;
  }

  private List<ActiveRuleParamDto> insertActiveRuleParams(DbSession session, org.sonar.api.rules.ActiveRule activeRule, RuleKey ruleKey, ActiveRuleDto activeRuleDto) {
    Map<String, String> valuesByParamKey = activeRule.getActiveRuleParams()
      .stream()
      .collect(MoreCollectors.uniqueIndex(ActiveRuleParam::getParamKey, ActiveRuleParam::getValue));
    return ruleRepository.getRuleParams(ruleKey)
      .stream()
      .map(param -> {
        String activeRuleValue = valuesByParamKey.get(param.getName());
        return createParamDto(param, activeRuleValue == null ? param.getDefaultValue() : activeRuleValue);
      })
      .filter(Objects::nonNull)
      .peek(paramDto -> dbClient.activeRuleDao().insertParam(session, activeRuleDto, paramDto))
      .collect(MoreCollectors.toList());
  }

  @CheckForNull
  private ActiveRuleParamDto createParamDto(RuleParamDto param, @Nullable String value) {
    if (value == null) {
      return null;
    }
    ActiveRuleParamDto paramDto = ActiveRuleParamDto.createFor(param);
    paramDto.setValue(validateParam(param, value));
    return paramDto;
  }

  @CheckForNull
  private String validateParam(RuleParamDto ruleParam, String value) {
    RuleParamType ruleParamType = RuleParamType.parse(ruleParam.getType());
    if (ruleParamType.multiple()) {
      List<String> values = newArrayList(Splitter.on(",").split(value));
      typeValidations.validate(values, ruleParamType.type(), ruleParamType.values());
    } else {
      typeValidations.validate(value, ruleParamType.type(), ruleParamType.values());
    }
    return value;
  }

  public static class RuleRepository {
    private final Map<RuleKey, RuleDefinitionDto> ruleDefinitions;
    private final Map<RuleKey, Set<RuleParamDto>> ruleParams;

    private RuleRepository(DbClient dbClient, DbSession session) {
      this.ruleDefinitions = dbClient.ruleDao().selectAllDefinitions(session)
        .stream()
        .collect(Collectors.toMap(RuleDefinitionDto::getKey, Function.identity()));
      Map<Integer, RuleKey> ruleIdsByKey = ruleDefinitions.values()
        .stream()
        .collect(MoreCollectors.uniqueIndex(RuleDefinitionDto::getId, RuleDefinitionDto::getKey));
      this.ruleParams = new HashMap<>(ruleIdsByKey.size());
      dbClient.ruleDao().selectRuleParamsByRuleKeys(session, ruleDefinitions.keySet())
        .forEach(ruleParam -> ruleParams.compute(
          ruleIdsByKey.get(ruleParam.getRuleId()),
          (key, value) -> {
            if (value == null) {
              return ImmutableSet.of(ruleParam);
            }
            return ImmutableSet.copyOf(Sets.union(value, Collections.singleton(ruleParam)));
          }));
    }

    Optional<RuleDefinitionDto> getDefinition(RuleKey ruleKey) {
      return Optional.ofNullable(ruleDefinitions.get(requireNonNull(ruleKey, "RuleKey can't be null")));
    }

    Set<RuleParamDto> getRuleParams(RuleKey ruleKey) {
      Set<RuleParamDto> res = ruleParams.get(requireNonNull(ruleKey, "RuleKey can't be null"));
      return res == null ? Collections.emptySet() : res;
    }
  }
}
