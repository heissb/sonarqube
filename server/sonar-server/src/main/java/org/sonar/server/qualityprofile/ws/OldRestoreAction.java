/*
 * SonarQube :: Server
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
package org.sonar.server.qualityprofile.ws;

import com.google.common.base.Preconditions;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Languages;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.text.JsonWriter;
import org.sonar.core.permission.GlobalPermissions;
import org.sonar.db.qualityprofile.QualityProfileDto;
import org.sonar.server.qualityprofile.BulkChangeResult;
import org.sonar.server.qualityprofile.QProfileBackuper;
import org.sonar.server.user.UserSession;
import org.sonar.server.ws.WsAction;

/**
 * @deprecated will be deleted once Orchestrator do not rely on this WS
 * It is duplicated to enable 
 */
@Deprecated
public class OldRestoreAction implements WsAction {

  private static final String PARAM_BACKUP = "backup";
  private final QProfileBackuper backuper;
  private final Languages languages;
  private final UserSession userSession;

  public OldRestoreAction(QProfileBackuper backuper, Languages languages, UserSession userSession) {
    this.backuper = backuper;
    this.languages = languages;
    this.userSession = userSession;
  }

  @Override
  public void define(WebService.NewController controller) {
    controller.createAction("restore")
      .setSince("5.2")
      .setDescription("Restore a quality profile using an XML file. The restored profile name is taken from the backup file, " +
        "so if a profile with the same name and language already exists, it will be overwritten. " +
        "Require Administer Quality Profiles permission.")
      .setPost(true)
      .setInternal(true)
      .setHandler(this)
      .createParam(PARAM_BACKUP)
      .setDescription("A profile backup file in XML format, as generated by api/qualityprofiles/backup " +
        "or the former api/profiles/backup.")
      .setRequired(true);
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    userSession.checkLoggedIn().checkPermission(GlobalPermissions.QUALITY_PROFILE_ADMIN);
    InputStream backup = request.paramAsInputStream(PARAM_BACKUP);
    InputStreamReader reader = null;

    try {
      Preconditions.checkArgument(backup != null, "A backup file must be provided");
      reader = new InputStreamReader(backup, StandardCharsets.UTF_8);
      BulkChangeResult result = backuper.restore(reader, null);
      writeResponse(response.newJsonWriter(), result);
    } finally {
      IOUtils.closeQuietly(reader);
      IOUtils.closeQuietly(backup);
    }
  }

  private void writeResponse(JsonWriter json, BulkChangeResult result) {
    QualityProfileDto profile = result.profile();
    if (profile != null) {
      String languageKey = profile.getLanguage();
      Language language = languages.get(languageKey);

      JsonWriter jsonProfile = json.beginObject().name("profile").beginObject();
      jsonProfile
        .prop("key", profile.getKey())
        .prop("name", profile.getName())
        .prop("language", languageKey)
        .prop("isDefault", false)
        .prop("isInherited", false);
      if (language != null) {
        jsonProfile.prop("languageName", language.getName());
      }
      jsonProfile.endObject();
    }
    json.prop("ruleSuccesses", result.countSucceeded());
    json.prop("ruleFailures", result.countFailed());
    json.endObject().close();
  }
}
