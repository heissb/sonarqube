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
// @flow
import React from 'react';
import classNames from 'classnames';
import moment from 'moment';
import { Link } from 'react-router';
import ProjectCardQualityGate from './ProjectCardQualityGate';
import ProjectCardLeakMeasures from './ProjectCardLeakMeasures';
import ProjectCardOverallMeasures from './ProjectCardOverallMeasures';
import FavoriteContainer from '../../../components/controls/FavoriteContainer';
import Organization from '../../../components/shared/Organization';
import TagsList from '../../../components/tags/TagsList';
import PrivateBadge from '../../../components/common/PrivateBadge';
import { translate, translateWithParameters } from '../../../helpers/l10n';

type Props = {
  measures: { [string]: string },
  organization?: { key: string },
  project?: {
    analysisDate?: string,
    key: string,
    name: string,
    tags: Array<string>,
    isFavorite?: boolean,
    organization?: string
  },
  type?: string
};

export default function ProjectCard({ measures, organization, project, type }: Props) {
  if (project == null) {
    return null;
  }

  const isProjectAnalyzed = project.analysisDate != null;
  const isLeakView = type === 'leak';

  let areProjectMeasuresLoaded;
  // check for particular measures because only some measures can be loaded
  // if coming from visualizations tab
  if (isLeakView) {
    areProjectMeasuresLoaded = measures != null && measures['new_bugs'];
  } else {
    areProjectMeasuresLoaded =
      measures != null &&
      measures['reliability_rating'] != null &&
      measures['sqale_rating'] != null;
  }

  const displayQualityGate = areProjectMeasuresLoaded && isProjectAnalyzed;
  const className = classNames('boxed-group', 'project-card', {
    'boxed-group-loading': isProjectAnalyzed && !areProjectMeasuresLoaded
  });

  return (
    <div data-key={project.key} className={className}>

      <div className="boxed-group-actions text-right">
        {project.visibility === 'private' &&
          <PrivateBadge className="spacer-left" tooltipPlacement="left" />}
        {project.tags.length > 0 && <TagsList tags={project.tags} customClass="spacer-left" />}
        {isLeakView &&
          isProjectAnalyzed &&
          <div className="little-spacer-top spacer-left note">
            {translateWithParameters(
              'overview.last_analysis_on_x',
              moment(project.analysisDate).format('LLL')
            )}
          </div>}
      </div>

      <div className="boxed-group-header">
        {project.isFavorite != null &&
          <FavoriteContainer className="spacer-right" componentKey={project.key} />}
        <h2 className="project-card-name">
          {organization == null &&
            project.organization != null &&
            <span className="text-normal">
              <Organization organizationKey={project.organization} />
            </span>}
          <Link to={{ pathname: '/dashboard', query: { id: project.key } }}>{project.name}</Link>
        </h2>
        {displayQualityGate && <ProjectCardQualityGate status={measures['alert_status']} />}
      </div>

      {isProjectAnalyzed
        ? <div className="boxed-group-inner">
            {areProjectMeasuresLoaded && isLeakView
              ? <ProjectCardLeakMeasures measures={measures} />
              : <ProjectCardOverallMeasures measures={measures} />}
          </div>
        : <div className="boxed-group-inner">
            <div className="note project-card-not-analyzed">
              {translate('projects.not_analyzed')}
            </div>
          </div>}
    </div>
  );
}
