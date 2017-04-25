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

package org.sonar.ce.cluster;

import org.sonar.api.config.Settings;
import org.sonar.process.ProcessProperties;

/**
 * Properties of the cluster configuration
 */
public final class ClusterClientProperties {
  private final boolean enabled;
  private final String name;
  private final String localEndPoint;

  public ClusterClientProperties(Settings settings) {
    enabled = settings.getBoolean(ProcessProperties.CLUSTER_ENABLED);
    name = settings.getString(ProcessProperties.CLUSTER_NAME);
    localEndPoint = settings.getString(ProcessProperties.CLUSTER_LOCALENDPOINT);
  }

  boolean isEnabled() {
    return enabled;
  }

  String getName() {
    return name;
  }

  public String getLocalEndPoint() {
    return localEndPoint;
  }
}
