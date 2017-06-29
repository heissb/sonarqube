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
import $ from 'jquery';
import { sortBy } from 'lodash';
import BaseFacet from './base-facet';
import Template from '../templates/facets/coding-rules-quality-profile-facet.hbs';

export default BaseFacet.extend({
  template: Template,

  events() {
    return {
      ...BaseFacet.prototype.events.apply(this, arguments),
      'click .js-active': 'setActivation',
      'click .js-inactive': 'unsetActivation'
    };
  },

  onRender() {
    this.$el.toggleClass('search-navigator-facet-box-collapsed', !this.model.get('enabled'));
    this.$el.attr('data-property', this.model.get('property'));
    const that = this;
    const property = this.model.get('property');
    const value = this.options.app.state.get('query')[property];
    if (typeof value === 'string') {
      value.split(',').forEach(s => {
        const facet = that.$('.js-facet').filter(`[data-value="${s}"]`);
        if (facet.length > 0) {
          facet.addClass('active');
        }
      });
    }
    const compareToProfile = this.options.app.state.get('query').compareToProfile;
    if (typeof compareToProfile === 'string') {
      const facet = that.$('.js-facet').filter(`[data-value="${compareToProfile}"]`);
      if (facet.length > 0) {
        facet.addClass('active compare');
      }
    }
  },

  getValues() {
    const that = this;
    const languagesQuery = this.options.app.state.get('query').languages;
    const languages = languagesQuery != null ? languagesQuery.split(',') : [];
    const lang = languages.length === 1 ? languages[0] : null;
    const values = this.options.app.qualityProfiles
      .filter(profile => (lang != null ? profile.lang === lang : true))
      .map(profile => ({
        extra: that.options.app.languages[profile.lang],
        isBuiltIn: profile.isBuiltIn,
        label: profile.name,
        val: profile.key
      }));
    return sortBy(values, 'label');
  },

  toggleFacet(e) {
    const obj = {};
    const property = this.model.get('property');
    if ($(e.currentTarget).is('.active')) {
      obj.activation = null;
      obj[property] = null;
    } else {
      obj.activation = true;
      obj[property] = $(e.currentTarget).data('value');
    }
    this.options.app.state.updateFilter(obj);
  },

  setActivation(e) {
    e.stopPropagation();
    const compareProfile = this.options.app.state.get('query').compareToProfile;
    const profile = $(e.currentTarget).parents('.js-facet').data('value');
    if (compareProfile == null || compareProfile !== profile) {
      this.options.app.state.updateFilter({ activation: 'true' });
    }
  },

  unsetActivation(e) {
    e.stopPropagation();
    const compareProfile = this.options.app.state.get('query').compareToProfile;
    const profile = $(e.currentTarget).parents('.js-facet').data('value');
    if (compareProfile == null || compareProfile !== profile) {
      this.options.app.state.updateFilter({
        activation: 'false',
        active_severities: null
      });
    }
  },

  getToggled() {
    const activation = this.options.app.state.get('query').activation;
    return activation === 'true' || activation === true;
  },

  disable() {
    const obj = { activation: null };
    const property = this.model.get('property');
    obj[property] = null;
    this.options.app.state.updateFilter(obj);
  },

  serializeData() {
    return {
      ...BaseFacet.prototype.serializeData.apply(this, arguments),
      values: this.getValues(),
      toggled: this.getToggled()
    };
  }
});
