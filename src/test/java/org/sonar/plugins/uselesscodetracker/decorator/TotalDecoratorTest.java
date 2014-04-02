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
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import org.sonar.plugins.uselesscodetracker.TrackerMetrics;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class TotalDecoratorTest {

  private TotalDecorator decorator;
  private DecoratorContext context;
  private Resource resource;

  @Before
  public void setUp() {
    decorator = new TotalDecorator();
    context = mock(DecoratorContext.class);
    resource = mock(Resource.class);
  }

  @Test
  public void shouldExecuteOnAnyProject() {
    Project project = new Project("key");
    assertThat(decorator.shouldExecuteOnProject(project), is(true));
  }

  @Test
  public void dependencies() {
    assertThat(decorator.dependedUpon(), hasItem(TrackerMetrics.TOTAL_USELESS_LINES));
    assertThat(decorator.dependsUpon(), hasItems(TrackerMetrics.USELESS_DUPLICATED_LINES));
  }

  @Test
  public void shouldNotDecorate() {
    when(resource.getScope())
        .thenReturn(Scopes.FILE)
        .thenReturn(Scopes.DIRECTORY);
    decorator.decorate(resource, context);
    verifyZeroInteractions(context);
  }

  @Test
  public void shouldDecorate() {
    when(resource.getScope()).thenReturn(Scopes.PROJECT);
    when(context.getMeasure(TrackerMetrics.USELESS_DUPLICATED_LINES)).thenReturn(new Measure(TrackerMetrics.USELESS_DUPLICATED_LINES, 1.0));
    decorator.decorate(resource, context);
    verify(context).saveMeasure(TrackerMetrics.TOTAL_USELESS_LINES, 1.0);
  }

  @Test
  public void shouldNotSaveZero() {
    when(resource.getScope()).thenReturn(Scopes.PROJECT);
    decorator.decorate(resource, context);
    verify(context, never()).saveMeasure(eq(TrackerMetrics.TOTAL_USELESS_LINES), anyDouble());
  }

}
