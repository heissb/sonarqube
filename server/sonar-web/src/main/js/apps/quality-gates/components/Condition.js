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
import React, { Component } from 'react';
import ThresholdInput from './ThresholdInput';
import DeleteConditionView from '../views/gate-conditions-delete-view';
import Checkbox from '../../../components/controls/Checkbox';
import { createCondition, updateCondition } from '../../../api/quality-gates';
import Select from '../../../components/controls/Select';
import { translate, getLocalizedMetricName } from '../../../helpers/l10n';
import { formatMeasure } from '../../../helpers/measures';

export default class Condition extends Component {
  constructor(props) {
    super(props);
    this.state = {
      changed: false,
      period: props.condition.period,
      op: props.condition.op,
      warning: props.condition.warning || '',
      error: props.condition.error || ''
    };
  }

  componentDidMount() {
    if (!this.props.condition.id && this.operator) {
      this.operator.focus();
    }
  }

  handleOperatorChange = ({ value }) => {
    this.setState({ changed: true, op: value });
  };

  handlePeriodChange = checked => {
    const period = checked ? '1' : undefined;
    this.setState({ changed: true, period });
  };

  handleWarningChange = value => {
    this.setState({ changed: true, warning: value });
  };

  handleErrorChange = value => {
    this.setState({ changed: true, error: value });
  };

  handleSaveClick = e => {
    const { qualityGate, condition, metric, onSaveCondition, onError, onResetError } = this.props;
    const { period } = this.state;
    const data = {
      metric: condition.metric,
      op: metric.type === 'RATING' ? 'GT' : this.state.op,
      warning: this.state.warning,
      error: this.state.error
    };

    if (period && metric.type !== 'RATING') {
      data.period = period;
    }

    if (metric.key.indexOf('new_') === 0) {
      data.period = '1';
    }

    e.preventDefault();
    createCondition(qualityGate.id, data)
      .then(newCondition => {
        this.setState({ changed: false });
        onSaveCondition(condition, newCondition);
        onResetError();
      })
      .catch(onError);
  };

  handleUpdateClick = e => {
    const { condition, onSaveCondition, metric, onError, onResetError } = this.props;
    const { period } = this.state;
    const data = {
      id: condition.id,
      metric: condition.metric,
      op: metric.type === 'RATING' ? 'GT' : this.state.op,
      warning: this.state.warning,
      error: this.state.error
    };

    if (period && metric.type !== 'RATING') {
      data.period = period;
    }

    if (metric.key.indexOf('new_') === 0) {
      data.period = '1';
    }

    e.preventDefault();
    updateCondition(data)
      .then(newCondition => {
        this.setState({ changed: false });
        onSaveCondition(condition, newCondition);
        onResetError();
      })
      .catch(onError);
  };

  handleDeleteClick = e => {
    const { qualityGate, condition, metric, onDeleteCondition } = this.props;

    e.preventDefault();
    new DeleteConditionView({
      qualityGate,
      condition,
      metric,
      onDelete: () => onDeleteCondition(condition)
    }).render();
  };

  handleCancelClick = e => {
    const { condition, onDeleteCondition } = this.props;

    e.preventDefault();
    onDeleteCondition(condition);
  };

  renderPeriodValue() {
    const { condition, metric } = this.props;
    const isLeakSelected = !!this.state.period;
    const isDiffMetric = condition.metric.indexOf('new_') === 0;
    const isRating = metric.type === 'RATING';

    if (isDiffMetric) {
      return (
        <span className="note">{translate('quality_gates.condition.leak.unconditional')}</span>
      );
    }

    if (isRating) {
      return <span className="note">{translate('quality_gates.condition.leak.never')}</span>;
    }

    return isLeakSelected
      ? translate('quality_gates.condition.leak.yes')
      : translate('quality_gates.condition.leak.no');
  }

  renderPeriod() {
    const { condition, metric, edit } = this.props;

    const isDiffMetric = condition.metric.indexOf('new_') === 0;
    const isRating = metric.type === 'RATING';
    const isLeakSelected = !!this.state.period;

    if (isRating || isDiffMetric || !edit) {
      return this.renderPeriodValue();
    }

    return <Checkbox checked={isLeakSelected} onCheck={this.handlePeriodChange} />;
  }

  renderOperator() {
    const { condition, edit, metric } = this.props;

    if (!edit) {
      return metric.type === 'RATING'
        ? translate('quality_gates.operator', condition.op, 'rating')
        : translate('quality_gates.operator', condition.op);
    }

    if (metric.type === 'RATING') {
      return <span className="note">{translate('quality_gates.operator.GT.rating')}</span>;
    }

    const operators = ['LT', 'GT', 'EQ', 'NE'];
    const operatorOptions = operators.map(op => {
      const label = translate('quality_gates.operator', op);
      return { label, value: op };
    });

    return (
      <Select
        className="input-medium"
        clearable={false}
        innerRef={node => (this.operator = node)}
        name="operator"
        onChange={this.handleOperatorChange}
        options={operatorOptions}
        searchable={false}
        value={this.state.op}
      />
    );
  }

  render() {
    const { condition, edit, metric } = this.props;
    return (
      <tr>
        <td className="text-middle">
          {getLocalizedMetricName(metric)}
          {metric.hidden && (
            <span className="text-danger little-spacer-left">{translate('deprecated')}</span>
          )}
        </td>

        <td className="thin text-middle nowrap">{this.renderPeriod()}</td>

        <td className="thin text-middle nowrap">{this.renderOperator()}</td>

        <td className="thin text-middle nowrap">
          {edit ? (
            <ThresholdInput
              name="warning"
              value={this.state.warning}
              metric={metric}
              onChange={this.handleWarningChange}
            />
          ) : (
            formatMeasure(condition.warning, metric.type)
          )}
        </td>

        <td className="thin text-middle nowrap">
          {edit ? (
            <ThresholdInput
              name="error"
              value={this.state.error}
              metric={metric}
              onChange={this.handleErrorChange}
            />
          ) : (
            formatMeasure(condition.error, metric.type)
          )}
        </td>

        {edit && (
          <td className="thin text-middle nowrap">
            {condition.id ? (
              <div>
                <button
                  className="update-condition"
                  disabled={!this.state.changed}
                  onClick={this.handleUpdateClick}>
                  {translate('update_verb')}
                </button>
                <button
                  className="button-red delete-condition little-spacer-left"
                  onClick={this.handleDeleteClick}>
                  {translate('delete')}
                </button>
              </div>
            ) : (
              <div>
                <button className="add-condition" onClick={this.handleSaveClick}>
                  {translate('add_verb')}
                </button>
                <a
                  className="cancel-add-condition spacer-left"
                  href="#"
                  onClick={this.handleCancelClick}>
                  {translate('cancel')}
                </a>
              </div>
            )}
          </td>
        )}
      </tr>
    );
  }
}
