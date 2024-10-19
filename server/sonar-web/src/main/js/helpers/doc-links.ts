/*
 * SonarQube
 * Copyright (C) 2009-2024 SonarSource SA
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

import { AlmKeys } from '../types/alm-settings';

export const DOC_URL = 'https://docs.sonarsource.com/sonarqube/latest';

export enum DocLink {
  AccountTokens = 'https://knowledgebase.autorabit.com/codescan/docs/generate-a-security-token/',
  ActiveVersions = '/server-upgrade-and-maintenance/upgrade/upgrade-the-server/active-versions/',
  AiCodeAssurance = 'https://knowledgebase.autorabit.com/codescan/docs',
  AlmAzureIntegration = '/devops-platform-integration/azure-devops-integration/',
  AlmBitBucketCloudAuth = '/instance-administration/authentication/bitbucket-cloud/',
  AlmBitBucketCloudIntegration = '/devops-platform-integration/bitbucket-integration/bitbucket-cloud-integration/',
  AlmBitBucketCloudSettings = '/instance-administration/authentication/bitbucket-cloud/#setting-your-authentication-settings-in-sonarqube',
  AlmBitBucketServerIntegration = '/devops-platform-integration/bitbucket-integration/bitbucket-server-integration/',
  AlmGitHubAuth = '/instance-administration/authentication/github/',
  AlmGitHubIntegration = '/devops-platform-integration/github-integration/introduction/',
  AlmGitHubMonorepoWorkfileExample = '/devops-platform-integration/github-integration/adding-analysis-to-github-actions-workflow/#configuring-the-buildyml-file',
  AlmGitLabAuth = '/instance-administration/authentication/gitlab/setting-up/',
  AlmGitLabAuthJITProvisioningMethod = '/instance-administration/authentication/gitlab/provisioning-modes/just-in-time/',
  AlmGitLabAuthAutoProvisioningMethod = '/instance-administration/authentication/gitlab/provisioning-modes/automatic/',
  AlmGitLabIntegration = '/devops-platform-integration/gitlab-integration/introduction/',
  AlmSamlAuth = '/instance-administration/authentication/saml/overview/',
  AlmSamlScimAuth = '/instance-administration/authentication/saml/scim/overview/',
  AnalysisScope = 'https://knowledgebase.autorabit.com/codescan/docs',
  AuthOverview = 'https://knowledgebase.autorabit.com/codescan/docs',
  BackgroundTasks = 'https://knowledgebase.autorabit.com/codescan/docs/background-tasks',
  BranchAnalysis = 'https://knowledgebase.autorabit.com/codescan/docs',
  CaYC = 'https://knowledgebase.autorabit.com/codescan/docs/',
  CFamilyBuildWrapper = 'https://knowledgebase.autorabit.com/codescan/docs',
  CFamilyCompilationDatabase = 'https://knowledgebase.autorabit.com/codescan/docs',
  CIAnalysisSetup = '/analyzing-source-code/ci-integration/overview/',
  CIJenkins = 'https://knowledgebase.autorabit.com/codescan/docs/use-jenkins-with-codescan-salesforce-project/',
  CleanCodeIntroduction = '/user-guide/clean-code/introduction/',
  CodeAnalysis = '/user-guide/clean-code/code-analysis/',
  InactiveBranches = '/project-administration/maintaining-the-branches-of-your-project/#manage-inactive-branches',
  InstanceAdminEncryption = 'https://knowledgebase.autorabit.com/codescan/docs',
  InstanceAdminLicense = '/instance-administration/license-administration/',
  InstanceAdminLoC = '/server-upgrade-and-maintenance/monitoring/lines-of-code/',
  InstanceAdminMarketplace = 'https://knowledgebase.autorabit.com/codescan/docs',
  InstanceAdminPluginVersionMatrix = '/setup-and-upgrade/plugins/plugin-version-matrix/',
  InstanceAdminQualityProfiles = 'https://knowledgebase.autorabit.com/codescan/docs/customising-quality-profiles',
  InstanceAdminQualityProfilesPrioritizingRules = '/instance-administration/analysis-functions/quality-profiles/#prioritizing-rules',
  InstanceAdminReindexation = '/server-upgrade-and-maintenance/maintenance/reindexing/',
  InstanceAdminSecurity = 'https://knowledgebase.autorabit.com/codescan/docs',
  IssueResolutions = '/user-guide/issues/solution-overview/#deprecated-features',
  Issues = '/user-guide/issues/introduction/',
  IssueStatuses = '/user-guide/issues/solution-overview/#life-cycle',
  MainBranchAnalysis = '/project-administration/maintaining-the-branches-of-your-project/',
  ManagingPortfolios = '/project-administration/managing-portfolios/',
  MetricDefinitions = '/user-guide/code-metrics/metrics-definition/',
  Monorepos = '/project-administration/monorepos/',
  NewCodeDefinition = '/project-administration/clean-as-you-code-settings/defining-new-code/',
  NewCodeDefinitionOptions = '/project-administration/clean-as-you-code-settings/defining-new-code/#new-code-definition-options',
  Portfolios = '/user-guide/viewing-reports/portfolios/',
  PullRequestAnalysis = 'https://knowledgebase.autorabit.com/codescan/docs',
  QualityGates = 'https://knowledgebase.autorabit.com/codescan/docs/customising-quality-gates',
  Root = '/',
  RulesOverview = '/user-guide/rules/overview',
  SecurityHotspots = 'https://knowledgebase.autorabit.com/codescan/docs',
  SecurityReports = '/user-guide/viewing-reports/security-reports/',
  ServerUpgradeRoadmap = 'https://knowledgebase.autorabit.com/codescan/docs/codescan-self-hosted',
  SonarLintConnectedMode = '/user-guide/sonarlint-connected-mode/',
  SonarScanner = 'https://knowledgebase.autorabit.com/codescan/docs',
  SonarScannerRequirements = '/analyzing-source-code/scanners/scanner-environment/general-requirements/',
  SonarScannerDotNet = 'https://knowledgebase.autorabit.com/codescan/docs',
  SonarScannerGradle = 'https://knowledgebase.autorabit.com/codescan/docs',
  SonarScannerMaven = 'https://knowledgebase.autorabit.com/codescan/docs',
  SonarWayQualityGate = '/user-guide/quality-gates/#using-sonar-way-the-recommended-quality-gate', // to be confirmed
  Webhooks = 'https://knowledgebase.autorabit.com/codescan/docs/webhooks/',
}

export const DocTitle = {
  [DocLink.BackgroundTasks]: 'About Background Tasks',
  [DocLink.CaYC]: 'Clean as You Code',
  [DocLink.CIAnalysisSetup]: 'Set up CI analysis',
  [DocLink.InstanceAdminQualityProfiles]: 'About Quality Profiles',
  [DocLink.MetricDefinitions]: 'Metric Definitions',
  [DocLink.NewCodeDefinition]: 'Defining New Code',
  [DocLink.PullRequestAnalysis]: 'Analyzing Pull Requests',
  [DocLink.SecurityReports]: 'About Security Reports',
  [DocLink.SonarLintConnectedMode]: 'SonarLint Connected Mode',
  [DocLink.Webhooks]: 'About Webhooks',
};

export type DocTitleKey = keyof typeof DocTitle;

const asDocSections = <T>(element: { [K in keyof T]: DocTitleKey[] }) => element;

export const DocSection = asDocSections({
  component_measures: [DocLink.CaYC, DocLink.MetricDefinitions],
  overview: [
    DocLink.PullRequestAnalysis,
    DocLink.CIAnalysisSetup,
    DocLink.CaYC,
    DocLink.SonarLintConnectedMode,
  ],
  pull_requests: [DocLink.CaYC, DocLink.PullRequestAnalysis, DocLink.SonarLintConnectedMode],
});

export type DocSectionKey = keyof typeof DocSection;

export const AlmAuthDocLinkKeys = {
  [AlmKeys.BitbucketServer]: DocLink.AlmBitBucketCloudAuth,
  [AlmKeys.GitHub]: DocLink.AlmGitHubAuth,
  [AlmKeys.GitLab]: DocLink.AlmGitLabAuth,
  saml: DocLink.AlmSamlAuth,
};
