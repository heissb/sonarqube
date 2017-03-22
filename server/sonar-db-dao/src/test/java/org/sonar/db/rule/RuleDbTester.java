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
package org.sonar.db.rule;

import java.util.Arrays;
import java.util.function.Consumer;
import org.sonar.db.DbTester;

public class RuleDbTester {

  private final DbTester db;

  public RuleDbTester(DbTester db) {
    this.db = db;
  }

  /**
   * Create and persist a rule with random values.
   */
  public RuleDto insertRule(Consumer<RuleDto>... consumers) {
    RuleDto rule = RuleTesting.newRuleDto();
    Arrays.stream(consumers).forEach(c -> c.accept(rule));
    return insertRule(rule);
  }

  public RuleDto insertRule(RuleDto ruleDto) {
    db.getDbClient().ruleDao().insert(db.getSession(), ruleDto);
    db.commit();
    return ruleDto;
  }
}
