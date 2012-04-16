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
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.uselesscodetracker.TrackerMetrics;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class DeadCodeDecoratorTest {

  private DeadCodeDecorator decorator;
  private DecoratorContext context;
  private Resource resource;

  @Before
  public void setUp() {
    decorator = new DeadCodeDecorator();
    context = mock(DecoratorContext.class);
    resource = mock(Resource.class);
  }

  @Test
  public void shouldExecuteOnAnyProject() {
    assertThat(decorator.shouldExecuteOnProject(new Project("key")), is(true));
  }

  @Test
  public void dependedUpon() {
    assertThat(decorator.dependedUpon(), hasItems(TrackerMetrics.DEAD_CODE, TrackerMetrics.POTENTIAL_DEAD_CODE));
  }

  @Test
  public void shouldNotDecorate() {
    when(resource.getQualifier()).thenReturn(Qualifiers.CLASS);
    decorator.decorate(resource, context);
    verifyZeroInteractions(context);
  }

  @Test
  public void shouldDecorate() {
    when(resource.getQualifier()).thenReturn(Qualifiers.PACKAGE);
    when(context.getChildrenMeasures(TrackerMetrics.DEAD_CODE))
        .thenReturn(Arrays.asList(
            new Measure(TrackerMetrics.DEAD_CODE, 1.0),
            new Measure(TrackerMetrics.DEAD_CODE, 2.0)));
    when(context.getChildrenMeasures(TrackerMetrics.POTENTIAL_DEAD_CODE))
        .thenReturn(Arrays.asList(
            new Measure(TrackerMetrics.POTENTIAL_DEAD_CODE, 3.0),
            new Measure(TrackerMetrics.POTENTIAL_DEAD_CODE, 4.0)));
    decorator.decorate(resource, context);
    verify(context).saveMeasure(TrackerMetrics.DEAD_CODE, 3.0);
    verify(context).saveMeasure(TrackerMetrics.POTENTIAL_DEAD_CODE, 7.0);
  }

  @Test
  public void shouldNotSaveZero() {
    when(resource.getQualifier()).thenReturn(Qualifiers.PACKAGE);
    decorator.decorate(resource, context);
    verify(context, never()).saveMeasure(eq(TrackerMetrics.DEAD_CODE), anyDouble());
    verify(context, never()).saveMeasure(eq(TrackerMetrics.POTENTIAL_DEAD_CODE), anyDouble());
  }

}
