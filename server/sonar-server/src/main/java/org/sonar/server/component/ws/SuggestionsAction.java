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
package org.sonar.server.component.ws;

import com.google.common.collect.ListMultimap;
import com.google.common.html.HtmlEscapers;
import com.google.common.io.Resources;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.sonar.api.server.ws.Change;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.server.ws.WebService.NewAction;
import org.sonar.core.util.stream.MoreCollectors;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.component.index.ComponentHit;
import org.sonar.server.component.index.ComponentHitsPerQualifier;
import org.sonar.server.component.index.ComponentIndex;
import org.sonar.server.component.index.ComponentIndexQuery;
import org.sonar.server.component.index.ComponentIndexResults;
import org.sonar.server.es.textsearch.ComponentTextSearchFeature;
import org.sonar.server.favorite.FavoriteFinder;
import org.sonar.server.user.UserSession;
import org.sonarqube.ws.WsComponents.SuggestionsWsResponse;
import org.sonarqube.ws.WsComponents.SuggestionsWsResponse.Category;
import org.sonarqube.ws.WsComponents.SuggestionsWsResponse.Project;
import org.sonarqube.ws.WsComponents.SuggestionsWsResponse.Suggestion;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static org.sonar.api.web.UserRole.USER;
import static org.sonar.core.util.stream.MoreCollectors.toList;
import static org.sonar.core.util.stream.MoreCollectors.toSet;
import static org.sonar.server.es.DefaultIndexSettings.MINIMUM_NGRAM_LENGTH;
import static org.sonar.server.ws.WsUtils.writeProtobuf;
import static org.sonarqube.ws.WsComponents.SuggestionsWsResponse.Organization;
import static org.sonarqube.ws.WsComponents.SuggestionsWsResponse.newBuilder;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.ACTION_SUGGESTIONS;

public class SuggestionsAction implements ComponentsWsAction {

  static final String PARAM_QUERY = "s";
  static final String PARAM_MORE = "more";
  static final String PARAM_RECENTLY_BROWSED = "recentlyBrowsed";
  static final String SHORT_INPUT_WARNING = "short_input";
  private static final int MAXIMUM_RECENTLY_BROWSED = 50;

  static final int EXTENDED_LIMIT = 20;

  private final ComponentIndex index;
  private final FavoriteFinder favoriteFinder;
  private final UserSession userSession;

  private DbClient dbClient;

  public SuggestionsAction(DbClient dbClient, ComponentIndex index, FavoriteFinder favoriteFinder, UserSession userSession) {
    this.dbClient = dbClient;
    this.index = index;
    this.favoriteFinder = favoriteFinder;
    this.userSession = userSession;
  }

  @Override
  public void define(WebService.NewController context) {
    NewAction action = context.createAction(ACTION_SUGGESTIONS)
      .setDescription(
        "Internal WS for the top-right search engine. The result will contain component search results, grouped by their qualifiers.<p>"
          + "Each result contains:"
          + "<ul>"
          + "<li>the organization key</li>"
          + "<li>the component key</li>"
          + "<li>the component's name (unescaped)</li>"
          + "<li>optionally a display name, which puts emphasis to matching characters (this text contains html tags and parts of the html-escaped name)</li>"
          + "</ul>")
      .setSince("4.2")
      .setInternal(true)
      .setHandler(this)
      .setResponseExample(Resources.getResource(this.getClass(), "components-example-suggestions.json"))
      .setChangelog(new Change("6.4", "Parameter 's' is optional"));

    action.createParam(PARAM_QUERY)
      .setRequired(false)
      .setDescription("Search query with a minimum of two characters. Can contain several search tokens, separated by spaces. " +
        "Search tokens with only one character will be ignored.")
      .setExampleValue("sonar");

    action.createParam(PARAM_MORE)
      .setDescription("Category, for which to display " + EXTENDED_LIMIT + " instead of " + ComponentIndexQuery.DEFAULT_LIMIT + " results")
      .setPossibleValues(stream(SuggestionCategory.values()).map(SuggestionCategory::getName).toArray(String[]::new))
      .setSince("6.4");

    action.createParam(PARAM_RECENTLY_BROWSED)
      .setDescription("Comma separated list of component keys, that have recently been browsed by the user. Only the first " + MAXIMUM_RECENTLY_BROWSED
        + " items will be used. Order is not taken into account.")
      .setSince("6.4")
      .setExampleValue("org.sonarsource:sonarqube,some.other:project")
      .setRequired(false)
      .setMaxValuesAllowed(MAXIMUM_RECENTLY_BROWSED);
  }

  @Override
  public void handle(Request wsRequest, Response wsResponse) throws Exception {
    String query = wsRequest.param(PARAM_QUERY);
    String more = wsRequest.param(PARAM_MORE);
    Set<String> recentlyBrowsedKeys = getRecentlyBrowsedKeys(wsRequest);
    List<String> qualifiers = getQualifiers(more);
    SuggestionsWsResponse searchWsResponse = loadSuggestions(query, more, recentlyBrowsedKeys, qualifiers);
    writeProtobuf(searchWsResponse, wsRequest, wsResponse);
  }

  private static Set<String> getRecentlyBrowsedKeys(Request wsRequest) {
    List<String> recentlyBrowsedParam = wsRequest.paramAsStrings(PARAM_RECENTLY_BROWSED);
    if (recentlyBrowsedParam == null) {
      return emptySet();
    }
    return new HashSet<>(recentlyBrowsedParam);
  }

  private SuggestionsWsResponse loadSuggestions(@Nullable String query, String more, Set<String> recentlyBrowsedKeys, List<String> qualifiers) {
    if (query == null) {
      return loadSuggestionsWithoutSearch(more, recentlyBrowsedKeys, qualifiers);
    }
    return loadSuggestionsWithSearch(query, more, recentlyBrowsedKeys, qualifiers);
  }

  /**
   * we are generating suggestions, by using (1) favorites and (2) recently browsed components (without searchin in Elasticsearch)
   */
  private SuggestionsWsResponse loadSuggestionsWithoutSearch(@Nullable String more, Set<String> recentlyBrowsedKeys, List<String> qualifiers) {
    List<ComponentDto> favoriteDtos = favoriteFinder.list();
    if (favoriteDtos.isEmpty() && recentlyBrowsedKeys.isEmpty()) {
      return newBuilder().build();
    }
    try (DbSession dbSession = dbClient.openSession(false)) {
      Set<ComponentDto> componentDtos = new HashSet<>(favoriteDtos);
      if (!recentlyBrowsedKeys.isEmpty()) {
        componentDtos.addAll(dbClient.componentDao().selectByKeys(dbSession, recentlyBrowsedKeys));
      }
      ListMultimap<String, ComponentDto> componentsPerQualifier = componentDtos.stream()
        .filter(c -> userSession.hasComponentPermission(USER, c))
        .collect(MoreCollectors.index(ComponentDto::qualifier));
      if (componentsPerQualifier.isEmpty()) {
        return newBuilder().build();
      }

      Set<String> favoriteUuids = favoriteDtos.stream().map(ComponentDto::uuid).collect(MoreCollectors.toSet(favoriteDtos.size()));
      Comparator<ComponentDto> favoriteComparator = Comparator.comparing(c -> favoriteUuids.contains(c.uuid()) ? -1 : +1);
      Comparator<ComponentDto> comparator = favoriteComparator.thenComparing(ComponentDto::name);

      int limit = more == null ? ComponentIndexQuery.DEFAULT_LIMIT : EXTENDED_LIMIT;
      ComponentIndexResults componentsPerQualifiers = ComponentIndexResults.newBuilder().setQualifiers(
        qualifiers.stream().map(q -> {
          List<ComponentHit> hits = componentsPerQualifier.get(q)
            .stream()
            .sorted(comparator)
            .limit(limit)
            .map(ComponentDto::uuid)
            .map(ComponentHit::new)
            .collect(MoreCollectors.toList(limit));
          int totalHits = componentsPerQualifier.size();
          return new ComponentHitsPerQualifier(q, hits, totalHits);
        })).build();
      return buildResponse(recentlyBrowsedKeys, favoriteUuids, componentsPerQualifiers, dbSession, componentDtos.stream()).build();
    }
  }

  private SuggestionsWsResponse loadSuggestionsWithSearch(String query, @Nullable String more, Set<String> recentlyBrowsedKeys, List<String> qualifiers) {
    List<ComponentDto> favorites = favoriteFinder.list();
    Set<String> favoriteKeys = favorites.stream().map(ComponentDto::getKey).collect(MoreCollectors.toSet(favorites.size()));
    ComponentIndexQuery.Builder queryBuilder = ComponentIndexQuery.builder()
      .setQuery(query)
      .setRecentlyBrowsedKeys(recentlyBrowsedKeys)
      .setFavoriteKeys(favoriteKeys)
      .setQualifiers(qualifiers);
    if (more != null) {
      queryBuilder.setLimit(EXTENDED_LIMIT);
    }
    ComponentIndexResults componentsPerQualifiers = searchInIndex(queryBuilder.build());
    if (componentsPerQualifiers.isEmpty()) {
      return newBuilder().build();
    }
    try (DbSession dbSession = dbClient.openSession(false)) {
      Set<String> componentUuids = componentsPerQualifiers.getQualifiers()
        .map(ComponentHitsPerQualifier::getHits)
        .flatMap(Collection::stream)
        .map(ComponentHit::getUuid)
        .collect(toSet());
      Stream<ComponentDto> componentDtoStream = dbClient.componentDao().selectByUuids(dbSession, componentUuids).stream();
      Set<String> favoriteUuids = favorites.stream().map(ComponentDto::uuid).collect(MoreCollectors.toSet(favorites.size()));
      SuggestionsWsResponse.Builder searchWsResponse = buildResponse(recentlyBrowsedKeys, favoriteUuids, componentsPerQualifiers, dbSession, componentDtoStream);
      getWarning(query).ifPresent(searchWsResponse::setWarning);
      return searchWsResponse.build();
    }
  }

  private static Optional<String> getWarning(String query) {
    List<String> tokens = ComponentTextSearchFeature.split(query).collect(Collectors.toList());
    if (tokens.stream().anyMatch(token -> token.length() < MINIMUM_NGRAM_LENGTH)) {
      return Optional.of(SHORT_INPUT_WARNING);
    }
    return Optional.empty();
  }

  private static List<String> getQualifiers(@Nullable String more) {
    if (more == null) {
      return stream(SuggestionCategory.values()).map(SuggestionCategory::getQualifier).collect(Collectors.toList());
    }
    return singletonList(SuggestionCategory.getByName(more).getQualifier());
  }

  private SuggestionsWsResponse.Builder buildResponse(Set<String> recentlyBrowsedKeys, Set<String> favoriteUuids, ComponentIndexResults componentsPerQualifiers, DbSession dbSession,
    Stream<ComponentDto> stream) {
    Map<String, ComponentDto> componentsByUuids = stream
      .collect(MoreCollectors.uniqueIndex(ComponentDto::uuid));
    Map<String, OrganizationDto> organizationsByUuids = loadOrganizations(dbSession, componentsByUuids.values());
    Map<String, ComponentDto> projectsByUuids = loadProjects(dbSession, componentsByUuids.values());
    return toResponse(componentsPerQualifiers, recentlyBrowsedKeys, favoriteUuids, organizationsByUuids, componentsByUuids, projectsByUuids);
  }

  private Map<String, ComponentDto> loadProjects(DbSession dbSession, Collection<ComponentDto> components) {
    Set<String> projectUuids = components.stream()
      .filter(c -> !c.projectUuid().equals(c.uuid()))
      .map(ComponentDto::projectUuid)
      .collect(MoreCollectors.toSet());
    return dbClient.componentDao().selectByUuids(dbSession, projectUuids).stream()
      .collect(MoreCollectors.uniqueIndex(ComponentDto::uuid));
  }

  private Map<String, OrganizationDto> loadOrganizations(DbSession dbSession, Collection<ComponentDto> components) {
    Set<String> organizationUuids = components.stream()
      .map(ComponentDto::getOrganizationUuid)
      .collect(MoreCollectors.toSet());
    return dbClient.organizationDao().selectByUuids(dbSession, organizationUuids).stream()
      .collect(MoreCollectors.uniqueIndex(OrganizationDto::getUuid));
  }

  private ComponentIndexResults searchInIndex(ComponentIndexQuery componentIndexQuery) {
    return index.search(componentIndexQuery);
  }

  private static SuggestionsWsResponse.Builder toResponse(ComponentIndexResults componentsPerQualifiers, Set<String> recentlyBrowsedKeys, Set<String> favoriteUuids,
    Map<String, OrganizationDto> organizationsByUuids, Map<String, ComponentDto> componentsByUuids, Map<String, ComponentDto> projectsByUuids) {
    if (componentsPerQualifiers.isEmpty()) {
      return newBuilder();
    }
    return newBuilder()
      .addAllResults(toCategories(componentsPerQualifiers, recentlyBrowsedKeys, favoriteUuids, componentsByUuids, organizationsByUuids, projectsByUuids))
      .addAllOrganizations(toOrganizations(organizationsByUuids))
      .addAllProjects(toProjects(projectsByUuids));
  }

  private static List<Category> toCategories(ComponentIndexResults componentsPerQualifiers, Set<String> recentlyBrowsedKeys, Set<String> favoriteUuids,
    Map<String, ComponentDto> componentsByUuids, Map<String, OrganizationDto> organizationByUuids, Map<String, ComponentDto> projectsByUuids) {
    return componentsPerQualifiers.getQualifiers().map(qualifier -> {

      List<Suggestion> suggestions = qualifier.getHits().stream()
        .map(hit -> toSuggestion(hit, recentlyBrowsedKeys, favoriteUuids, componentsByUuids, organizationByUuids, projectsByUuids))
        .collect(toList());

      return Category.newBuilder()
        .setQ(qualifier.getQualifier())
        .setMore(qualifier.getNumberOfFurtherResults())
        .addAllItems(suggestions)
        .build();
    }).collect(toList());
  }

  private static Suggestion toSuggestion(ComponentHit hit, Set<String> recentlyBrowsedKeys, Set<String> favoriteUuids, Map<String, ComponentDto> componentsByUuids,
    Map<String, OrganizationDto> organizationByUuids, Map<String, ComponentDto> projectsByUuids) {
    ComponentDto result = componentsByUuids.get(hit.getUuid());
    String organizationKey = organizationByUuids.get(result.getOrganizationUuid()).getKey();
    checkState(organizationKey != null, "Organization with uuid '%s' not found", result.getOrganizationUuid());
    String projectKey = ofNullable(result.projectUuid()).map(projectsByUuids::get).map(ComponentDto::getKey).orElse("");
    return Suggestion.newBuilder()
      .setOrganization(organizationKey)
      .setProject(projectKey)
      .setKey(result.getKey())
      .setName(result.longName())
      .setMatch(hit.getHighlightedText().orElse(HtmlEscapers.htmlEscaper().escape(result.longName())))
      .setIsRecentlyBrowsed(recentlyBrowsedKeys.contains(result.getKey()))
      .setIsFavorite(favoriteUuids.contains(result.uuid()))
      .build();
  }

  private static List<Organization> toOrganizations(Map<String, OrganizationDto> organizationByUuids) {
    return organizationByUuids.values().stream()
      .map(o -> Organization.newBuilder()
        .setKey(o.getKey())
        .setName(o.getName())
        .build())
      .collect(Collectors.toList());
  }

  private static List<Project> toProjects(Map<String, ComponentDto> projectsByUuids) {
    return projectsByUuids.values().stream()
      .map(p -> Project.newBuilder()
        .setKey(p.key())
        .setName(p.longName())
        .build())
      .collect(Collectors.toList());
  }
}
