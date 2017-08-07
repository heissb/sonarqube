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
package org.sonar.scanner.report;

import java.util.Optional;
import org.sonar.api.batch.AnalysisMode;
import org.sonar.api.batch.bootstrap.ProjectDefinition;
import org.sonar.api.batch.fs.internal.DefaultInputModule;
import org.sonar.api.batch.fs.internal.InputModuleHierarchy;
import org.sonar.api.config.Configuration;
import org.sonar.scanner.ProjectAnalysisInfo;
import org.sonar.scanner.cpd.CpdSettings;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.scanner.protocol.output.ScannerReport.Metadata.BranchType;
import org.sonar.scanner.protocol.output.ScannerReportWriter;
import org.sonar.scanner.rule.ModuleQProfiles;
import org.sonar.scanner.rule.QProfile;
import org.sonar.scanner.scan.ProjectBranches;

import static org.sonar.core.config.ScannerProperties.BRANCH_NAME;
import static org.sonar.core.config.ScannerProperties.ORGANIZATION;

public class MetadataPublisher implements ReportPublisherStep {

  private final Configuration settings;
  private final ModuleQProfiles qProfiles;
  private final ProjectAnalysisInfo projectAnalysisInfo;
  private final InputModuleHierarchy moduleHierarchy;
  private final CpdSettings cpdSettings;
  private final AnalysisMode mode;
  private final ProjectBranches branches;

  public MetadataPublisher(ProjectAnalysisInfo projectAnalysisInfo, InputModuleHierarchy moduleHierarchy, Configuration settings,
    ModuleQProfiles qProfiles, CpdSettings cpdSettings, AnalysisMode mode, ProjectBranches branches) {
    this.projectAnalysisInfo = projectAnalysisInfo;
    this.moduleHierarchy = moduleHierarchy;
    this.settings = settings;
    this.qProfiles = qProfiles;
    this.cpdSettings = cpdSettings;
    this.mode = mode;
    this.branches = branches;
  }

  @Override
  public void publish(ScannerReportWriter writer) {
    DefaultInputModule rootProject = moduleHierarchy.root();
    ProjectDefinition rootDef = rootProject.definition();
    ScannerReport.Metadata.Builder builder = ScannerReport.Metadata.newBuilder()
      .setAnalysisDate(projectAnalysisInfo.analysisDate().getTime())
      // Here we want key without branch
      .setProjectKey(rootDef.getKey())
      .setCrossProjectDuplicationActivated(cpdSettings.isCrossProjectDuplicationEnabled())
      .setRootComponentRef(rootProject.batchId())
      .setIncremental(mode.isIncremental());

    settings.get(ORGANIZATION).ifPresent(builder::setOrganizationKey);
    settings.get(BRANCH_NAME).ifPresent(branch -> {
      builder.setBranchName(branch);
      builder.setBranchType(toProtobufBranchType(branches.branchType()));
      String branchTarget = branches.branchTarget();
      if (branchTarget != null) {
        builder.setMergeBranchName(branchTarget);
      }
    });
    Optional.ofNullable(rootDef.getBranch()).ifPresent(builder::setDeprecatedBranch);

    for (QProfile qp : qProfiles.findAll()) {
      builder.getMutableQprofilesPerLanguage().put(qp.getLanguage(), ScannerReport.Metadata.QProfile.newBuilder()
        .setKey(qp.getKey())
        .setLanguage(qp.getLanguage())
        .setName(qp.getName())
        .setRulesUpdatedAt(qp.getRulesUpdatedAt().getTime()).build());
    }
    writer.writeMetadata(builder.build());
  }

  private static BranchType toProtobufBranchType(ProjectBranches.BranchType branchType) {
    if (branchType == ProjectBranches.BranchType.LONG) {
      return BranchType.LONG;
    }
    return BranchType.SHORT;
  }
}
