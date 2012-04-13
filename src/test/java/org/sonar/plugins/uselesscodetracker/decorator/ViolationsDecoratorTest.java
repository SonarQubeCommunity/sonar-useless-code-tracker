/*
 * Sonar Useless Code Tracker Plugin
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.uselesscodetracker.decorator;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.RuleFinder;
import org.sonar.plugins.uselesscodetracker.TrackerMetrics;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ViolationsDecoratorTest {

  private ViolationsDecorator decorator;

  @Before
  public void setUp() {
    decorator = new ViolationsDecorator(mock(RulesProfile.class), mock(RuleFinder.class));
  }

  @Test
  public void shouldExecuteOnlyOnJavaProject() {
    Project project = new Project("key");
    project.setLanguageKey("java");
    assertThat(decorator.shouldExecuteOnProject(project), is(true));
    project.setLanguageKey("groovy");
    assertThat(decorator.shouldExecuteOnProject(project), is(false));
  }

  @Test
  public void dependencies() {
    assertThat(decorator.dependedUpon(), hasItems(TrackerMetrics.DEAD_CODE, TrackerMetrics.POTENTIAL_DEAD_CODE));
    assertThat(decorator.dependsUpon(), hasItem(TrackerMetrics.TEMP_METHOD_LINES));
  }

}
