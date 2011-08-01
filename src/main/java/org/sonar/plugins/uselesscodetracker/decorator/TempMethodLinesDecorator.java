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

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.*;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import org.sonar.java.api.JavaMethod;
import org.sonar.plugins.uselesscodetracker.TrackerMetrics;

import java.util.Arrays;
import java.util.List;


public class TempMethodLinesDecorator implements Decorator {

  @DependedUpon
  public List<Metric> dependedUpon() {
    return Arrays.asList(TrackerMetrics.TEMP_METHOD_LINES);
  }


  public void decorate(Resource resource, DecoratorContext context) {
    if (resource instanceof JavaMethod) {
      computeMethodDistribution(resource, context);
      return;
    }
    if (!Scopes.isHigherThan(resource, Scopes.FILE)) {
      computeDistributionFromChildren(context);
    }
  }


  protected void computeDistributionFromChildren(DecoratorContext context) {
    CountDistributionBuilder builder = new CountDistributionBuilder(TrackerMetrics.TEMP_METHOD_LINES);
    for (Measure childMeasure : context.getChildrenMeasures(TrackerMetrics.TEMP_METHOD_LINES)) {
      builder.add(childMeasure);
    }
    context.saveMeasure(builder.build().setPersistenceMode(PersistenceMode.MEMORY));
  }

  private void computeMethodDistribution(Resource resource, DecoratorContext context) {
    JavaMethod method = (JavaMethod) resource;

    int lineNumber = method.getFromLine();
    int numberOfLines = MeasureUtils.getValue(context.getMeasure(CoreMetrics.LINES), 0.0).intValue();

    CountDistributionBuilder builder = new CountDistributionBuilder(TrackerMetrics.TEMP_METHOD_LINES);
    builder.add(lineNumber, numberOfLines);
    context.saveMeasure(builder.build().setPersistenceMode(PersistenceMode.MEMORY));
  }

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }
}
