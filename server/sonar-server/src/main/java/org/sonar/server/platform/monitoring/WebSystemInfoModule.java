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
package org.sonar.server.platform.monitoring;

import org.sonar.process.systeminfo.JvmPropertiesSection;
import org.sonar.process.systeminfo.JvmStateSection;
import org.sonar.server.platform.monitoring.cluster.AppNodesInfoLoaderImpl;
import org.sonar.server.platform.monitoring.cluster.GlobalInfoLoader;
import org.sonar.server.platform.monitoring.cluster.GlobalSystemSection;
import org.sonar.server.platform.monitoring.cluster.LoggingSection;
import org.sonar.server.platform.monitoring.cluster.NodeSystemSection;
import org.sonar.server.platform.monitoring.cluster.ProcessInfoProviderImpl;
import org.sonar.server.platform.monitoring.cluster.SearchNodesInfoLoaderImpl;
import org.sonar.server.platform.ws.ClusterInfoAction;

public class WebSystemInfoModule {

  private WebSystemInfoModule() {
    // do not instantiate
  }

  public static Object[] forStandaloneMode() {
    return new Object[] {
      new JvmPropertiesSection("Web JVM Properties"),
      new JvmStateSection("Web JVM State"),
      DatabaseSection.class,
      EsStateSection.class,
      EsStatisticsSection.class,
      PluginsSection.class,
      SettingsSection.class,
      StandaloneSystemSection.class,

      OfficialDistribution.class,
    };
  }

  public static Object[] forClusterMode() {
    return new Object[] {
      new JvmPropertiesSection("Web JVM Properties"),
      new JvmStateSection("Web JVM State"),
      DatabaseSection.class,
      EsStatisticsSection.class,
      GlobalSystemSection.class,
      LoggingSection.class,
      NodeSystemSection.class,
      PluginsSection.class,
      SettingsSection.class,

      OfficialDistribution.class,

      ProcessInfoProviderImpl.class,
      GlobalInfoLoader.class,
      AppNodesInfoLoaderImpl.class,
      SearchNodesInfoLoaderImpl.class,
      ClusterInfoAction.class
    };
  }
}
