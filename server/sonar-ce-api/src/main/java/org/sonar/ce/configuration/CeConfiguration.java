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
package org.sonar.ce.configuration;

public interface CeConfiguration {

  /**
   * The number of workers to process CeTasks concurrently.
   */
  int getWorkerCount();

  /**
   * The delay in millisecond before a {@link CeWorker} shall try and find a task
   * to process when it's previous execution had nothing to do.
   */
  long getQueuePollingDelay();

  /**
   * Delay before running job that cancels worn out tasks for the first time (in minutes).
   */
  long getCancelWornOutsInitialDelay();

  /**
   * Delay between the end of a run and the start of the next one of the job that cancels worn out CE tasks (in minutes).
   */
  long getCancelWornOutsDelay();

  /**
   * Delay before running job that reset tasks that does not have an existing worker UUID (in minutes).
   */
  long getTasksWithUnknownWorkerUUIDsInitialDelay();

  /**
   * Delay between the end of a run and the start of the next one of the job that reset tasks that does not have
   * an existing worker UUID (in minutes).
   */
  long getTasksWithUnknownWorkerUUIDsDelay();
}
