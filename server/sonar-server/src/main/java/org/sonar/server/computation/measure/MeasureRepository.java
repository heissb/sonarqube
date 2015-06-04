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
package org.sonar.server.computation.measure;

import com.google.common.base.Optional;
import org.sonar.api.measures.Metric;
import org.sonar.server.computation.component.Component;

public interface MeasureRepository {

  /**
   * Retrieves the last measure saved in DB for the specified {@link Component} for the specified {@link Metric} if it
   * exists.
   */
  Optional<Measure> findPrevious(Component component, Metric<?> metric);

  /**
   * Retrieves the measure created during the current analysis for the specified {@link Component} for the specified
   * {@link Metric} if it exists (ie. one created by the Compute Engine or the Batch).
   */
  Optional<Measure> findCurrent(Component component, Metric<?> metric);
}
