/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
package org.sonar.server.rule.index;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.sonar.api.config.Settings;
import org.sonar.server.es.IndexDefinition;
import org.sonar.server.es.NewIndex;

/**
 * Definition of ES index "rules", including settings and fields.
 */
public class RuleIndexDefinition implements IndexDefinition {

  public static final String INDEX = "rules";
  public static final String TYPE_RULE = "rule";

  public static final String FIELD_RULE_KEY = "key";
  public static final String FIELD_RULE_REPOSITORY = "repo";
  public static final String FIELD_RULE_RULE_KEY = "ruleKey";
  public static final String FIELD_RULE_INTERNAL_KEY = "internalKey";
  public static final String FIELD_RULE_NAME = "name";
  public static final String FIELD_RULE_HTML_DESCRIPTION = "htmlDesc";
  public static final String FIELD_RULE_SEVERITY = "severity";
  public static final String FIELD_RULE_STATUS = "status";
  public static final String FIELD_RULE_LANGUAGE = "lang";
  public static final String FIELD_RULE_IS_TEMPLATE = "isTemplate";
  public static final String FIELD_RULE_TEMPLATE_KEY = "templateKey";
  public static final String FIELD_RULE_ALL_TAGS = "allTags";
  public static final String FIELD_RULE_TYPE = "type";
  public static final String FIELD_RULE_CREATED_AT = "createdAt";
  public static final String FIELD_RULE_UPDATED_AT = "updatedAt";

  public static final Set<String> SORT_FIELDS = ImmutableSet.of(
    RuleIndexDefinition.FIELD_RULE_NAME,
    RuleIndexDefinition.FIELD_RULE_UPDATED_AT,
    RuleIndexDefinition.FIELD_RULE_CREATED_AT,
    RuleIndexDefinition.FIELD_RULE_KEY
  );

  // Active rule fields

  public static final String TYPE_ACTIVE_RULE = "activeRule";
  public static final String FIELD_ACTIVE_RULE_KEY = "key";
  public static final String FIELD_ACTIVE_RULE_REPOSITORY = "repo";
  public static final String FIELD_ACTIVE_RULE_INHERITANCE = "inheritance";
  public static final String FIELD_ACTIVE_RULE_PROFILE_KEY = "profile";
  public static final String FIELD_ACTIVE_RULE_SEVERITY = "severity";
  public static final String FIELD_ACTIVE_RULE_RULE_KEY = "ruleKey";
  public static final String FIELD_ACTIVE_RULE_CREATED_AT = "createdAt";
  public static final String FIELD_ACTIVE_RULE_UPDATED_AT = "updatedAt";

  private final Settings settings;

  public RuleIndexDefinition(Settings settings) {
    this.settings = settings;
  }

  @Override
  public void define(IndexDefinitionContext context) {
    NewIndex index = context.create(INDEX);

    index.refreshHandledByIndexer();
    index.configureShards(settings);

    // Rule type
    NewIndex.NewIndexType ruleMapping = index.createType(TYPE_RULE);
    ruleMapping.setAttribute("_id", ImmutableMap.of("path", FIELD_RULE_KEY));
    ruleMapping.setAttribute("_routing", ImmutableMap.of("required", true, "path", RuleIndexDefinition.FIELD_RULE_REPOSITORY));
    ruleMapping.setEnableSource(false);

    ruleMapping.stringFieldBuilder(FIELD_RULE_KEY).enableSorting().build();
    ruleMapping.stringFieldBuilder(FIELD_RULE_RULE_KEY).enableSorting().build();
    ruleMapping.stringFieldBuilder(FIELD_RULE_REPOSITORY).docValues().build();
    ruleMapping.stringFieldBuilder(FIELD_RULE_INTERNAL_KEY).disableSearch().docValues().build();

    ruleMapping.stringFieldBuilder(FIELD_RULE_NAME).enableSorting().enableWordSearch().build();
    ruleMapping.stringFieldBuilder(FIELD_RULE_HTML_DESCRIPTION).enableWordSearch().build();
    ruleMapping.stringFieldBuilder(FIELD_RULE_SEVERITY).docValues().build();
    ruleMapping.stringFieldBuilder(FIELD_RULE_STATUS).docValues().build();
    ruleMapping.stringFieldBuilder(FIELD_RULE_LANGUAGE).build();

    ruleMapping.createBooleanField(FIELD_RULE_IS_TEMPLATE);
    ruleMapping.stringFieldBuilder(FIELD_RULE_TEMPLATE_KEY).docValues().build();

    ruleMapping.stringFieldBuilder(FIELD_RULE_ALL_TAGS).enableGramSearch().build();
    ruleMapping.stringFieldBuilder(FIELD_RULE_TYPE).build();

    ruleMapping.createLongField(FIELD_RULE_CREATED_AT);
    ruleMapping.createLongField(FIELD_RULE_UPDATED_AT);

    // Active rule type
    NewIndex.NewIndexType activeRuleMapping = index.createType(RuleIndexDefinition.TYPE_ACTIVE_RULE);
    activeRuleMapping.setEnableSource(false);
    activeRuleMapping.setAttribute("_id", ImmutableMap.of("path", RuleIndexDefinition.FIELD_ACTIVE_RULE_KEY));
    activeRuleMapping.setAttribute("_parent", ImmutableMap.of("type", RuleIndexDefinition.TYPE_RULE));
    activeRuleMapping.setAttribute("_routing", ImmutableMap.of("required", true, "path", RuleIndexDefinition.FIELD_ACTIVE_RULE_REPOSITORY));

    activeRuleMapping.stringFieldBuilder(RuleIndexDefinition.FIELD_ACTIVE_RULE_KEY).enableSorting().build();
    activeRuleMapping.stringFieldBuilder(RuleIndexDefinition.FIELD_ACTIVE_RULE_RULE_KEY).disableSearch().docValues().build();
    activeRuleMapping.stringFieldBuilder(RuleIndexDefinition.FIELD_ACTIVE_RULE_REPOSITORY).disableSearch().docValues().build();
    activeRuleMapping.stringFieldBuilder(RuleIndexDefinition.FIELD_ACTIVE_RULE_PROFILE_KEY).docValues().build();
    activeRuleMapping.stringFieldBuilder(RuleIndexDefinition.FIELD_ACTIVE_RULE_INHERITANCE).docValues().build();
    activeRuleMapping.stringFieldBuilder(RuleIndexDefinition.FIELD_ACTIVE_RULE_SEVERITY).docValues().build();

    activeRuleMapping.createLongField(RuleIndexDefinition.FIELD_ACTIVE_RULE_CREATED_AT);
    activeRuleMapping.createLongField(RuleIndexDefinition.FIELD_ACTIVE_RULE_UPDATED_AT);
  }
}
