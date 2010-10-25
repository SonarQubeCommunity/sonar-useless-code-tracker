/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.uselesscodetracker.decorator;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.plugins.uselesscodetracker.TrackerMetrics;

import java.util.Arrays;
import java.util.List;

public class TotalDecorator implements Decorator {

  @DependsUpon
  public List<Metric> dependsUpon() {
    return Arrays.asList(TrackerMetrics.USELESS_DUPLICATED_LINES, TrackerMetrics.DEAD_CODE, TrackerMetrics.POTENTIAL_DEAD_CODE);
  }

  @DependedUpon
  public List<Metric> DependedUpon() {
    return Arrays.asList(TrackerMetrics.TOTAL_USELESS_LINES);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    if( !ResourceUtils.isFile(resource) && ! ResourceUtils.isPackage(resource)){
      double lines = 0.0;
      double duplicated = MeasureUtils.getValue(context.getMeasure(TrackerMetrics.USELESS_DUPLICATED_LINES), 0.0);
      double deadCode = MeasureUtils.getValue(context.getMeasure(TrackerMetrics.DEAD_CODE), 0.0);
      double potentialDeadCode = MeasureUtils.getValue(context.getMeasure(TrackerMetrics.POTENTIAL_DEAD_CODE), 0.0);
      lines += duplicated + deadCode + potentialDeadCode;
      context.saveMeasure(TrackerMetrics.TOTAL_USELESS_LINES, lines);
    }
  }

  public boolean shouldExecuteOnProject(Project project) {
    if(Java.INSTANCE.equals(project.getLanguage())) {
      return true;
    }
    return false;
  }
}
