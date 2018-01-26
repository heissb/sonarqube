/*
 * SonarQube
 * Copyright (C) 2009-2018 SonarSource SA
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
package org.sonarqube.qa.util.pageobjects;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class RuleItem {

  private final SelenideElement elt;

  public RuleItem(SelenideElement elt) {
    this.elt = elt;
  }

  public SelenideElement getTitle() {
    return elt.$(".coding-rule-title");
  }

  public SelenideElement getMetadata() {
    return elt.$(".coding-rule-meta");
  }

  public RuleItem filterSimilarRules(String property) {
    elt.$(".js-rule-filter").click();
    $(".issue-action-option[data-property=\"" + property + "\"]").click();
    return this;
  }

  public RuleDetails open() {
    elt.$(".js-rule").click();
    return new RuleDetails();
  }

}
