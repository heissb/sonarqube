/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.qualityprofile.ws;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService.NewAction;
import org.sonar.api.server.ws.WebService.NewController;
import org.sonar.api.utils.Paging;
import org.sonar.api.utils.text.JsonWriter;
import org.sonar.api.web.UserRole;
import org.sonar.core.persistence.DbSession;
import org.sonar.core.qualityprofile.db.ProjectQprofileAssociationDto;
import org.sonar.core.util.NonNullInputFunction;
import org.sonar.server.db.DbClient;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.user.UserSession;

import java.util.Collection;
import java.util.List;

public class QProfileProjectsAction implements BaseQProfileWsAction {

  private static final String PARAM_KEY = "key";
  private static final String PARAM_SELECTED = "selected";
  private static final String PARAM_QUERY = "query";
  private static final String PARAM_PAGE_SIZE = "pageSize";
  private static final String PARAM_PAGE = "page";

  private static final String SELECTION_ALL = "all";
  private static final String SELECTION_SELECTED = "selected";
  private static final String SELECTION_DESELECTED = "deselected";

  private final DbClient dbClient;

  public QProfileProjectsAction(DbClient dbClient) {
    this.dbClient = dbClient;
  }

  @Override
  public void define(NewController controller) {
    NewAction projects = controller.createAction("projects")
      .setSince("5.2")
      .setHandler(this)
      .setDescription("List projects with their association status regarding a quality profile.");
    projects.createParam(PARAM_KEY)
      .setDescription("A quality profile key.")
      .setRequired(true)
      .setExampleValue("sonar-way-java-12345");
    projects.createParam(PARAM_SELECTED)
      .setDescription("If specified, return only selected or deselected projects.")
      .setPossibleValues(SELECTION_SELECTED, SELECTION_DESELECTED, SELECTION_ALL)
      .setDefaultValue(SELECTION_ALL);
    projects.createParam(PARAM_QUERY)
      .setDescription("If specified, return only projects whose name match the query.");
    projects.createParam(PARAM_PAGE_SIZE)
      .setDescription("Size for the paging to apply").setDefaultValue(100);
    projects.createParam(PARAM_PAGE)
      .setDescription("Index of the page to display").setDefaultValue(1);
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    String profileKey = request.mandatoryParam(PARAM_KEY);

    DbSession session = dbClient.openSession(false);

    try {
      checkProfileExists(profileKey, session);
      String selected = request.param(PARAM_SELECTED);
      String query = request.param(PARAM_QUERY);
      int pageSize = request.mandatoryParamAsInt(PARAM_PAGE_SIZE);
      int page = request.mandatoryParamAsInt(PARAM_PAGE);

      List<ProjectQprofileAssociationDto> projects = loadProjects(profileKey, session, selected, query);

      Collection<Long> projectIds = Collections2.transform(projects, new NonNullInputFunction<ProjectQprofileAssociationDto, Long>() {
        @Override
        protected Long doApply(ProjectQprofileAssociationDto input) {
          return input.getProjectId();
        }
      });

      final Collection<Long> authorizedProjectIds = dbClient.authorizationDao().keepAuthorizedProjectIds(session, projectIds, UserSession.get().userId(), UserRole.USER);
      Iterable<ProjectQprofileAssociationDto> authorizedProjects = Iterables.filter(projects, new Predicate<ProjectQprofileAssociationDto>() {
        @Override
        public boolean apply(ProjectQprofileAssociationDto input) {
          return authorizedProjectIds.contains(input.getProjectId());
        }
      });

      Paging paging = Paging.create(pageSize, page, authorizedProjectIds.size());

      List<ProjectQprofileAssociationDto> pagedAuthorizedProjects = Lists.newArrayList(authorizedProjects);
      if (pagedAuthorizedProjects.size() <= paging.offset()) {
        pagedAuthorizedProjects = Lists.newArrayList();
      } else if (pagedAuthorizedProjects.size() > paging.pageSize()) {
        pagedAuthorizedProjects = pagedAuthorizedProjects.subList(paging.offset(), paging.offset() + pageSize);
      }

      writeProjects(response.newJsonWriter(), pagedAuthorizedProjects, paging);
    } finally {
      session.close();
    }
  }

  private void checkProfileExists(String profileKey, DbSession session) {
    try {
      dbClient.qualityProfileDao().getNonNullByKey(session, profileKey);
    } catch (IllegalArgumentException doesNotExist) {
      NotFoundException notFoundException = new NotFoundException(String.format("Could not find a quality profile with key '%s'", profileKey));
      notFoundException.initCause(doesNotExist);
      throw notFoundException;
    }
  }

  private List<ProjectQprofileAssociationDto> loadProjects(String profileKey, DbSession session, String selected, String query) {
    List<ProjectQprofileAssociationDto> projects = Lists.newArrayList();
    if (SELECTION_SELECTED.equals(selected)) {
      projects.addAll(dbClient.qualityProfileDao().selectSelectedProjects(profileKey, query, session));
    } else if (SELECTION_DESELECTED.equals(selected)) {
      projects.addAll(dbClient.qualityProfileDao().selectDeselectedProjects(profileKey, query, session));
    } else {
      projects.addAll(dbClient.qualityProfileDao().selectProjectAssociations(profileKey, query, session));
    }
    return projects;
  }

  private void writeProjects(JsonWriter json, List<ProjectQprofileAssociationDto> projects, Paging paging) {
    json.beginObject();
    json.name("results").beginArray();
    for (ProjectQprofileAssociationDto project : projects) {
      json.beginObject()
        .prop("key", project.getProjectUuid())
        .prop("name", project.getProjectName())
        .prop("selected", project.isAssociated())
        .endObject();
    }
    json.endArray();
    json.prop("more", paging.hasNextPage());
    json.endObject().close();
  }
}
