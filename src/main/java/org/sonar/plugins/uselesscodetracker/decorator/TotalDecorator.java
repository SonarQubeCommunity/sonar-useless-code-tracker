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
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.plugins.uselesscodetracker.TrackerMetrics;

import java.util.Arrays;
import java.util.List;

public class TotalDecorator implements Decorator {

  @DependsUpon
  public List<Metric> dependsUpon() {
    return Arrays.asList(TrackerMetrics.USELESS_DUPLICATED_LINES);
  }

  @DependedUpon
  public List<Metric> dependedUpon() {
    return Arrays.asList(TrackerMetrics.TOTAL_USELESS_LINES);
  }

  @Override
  public void decorate(Resource resource, DecoratorContext context) {
    if (!ResourceUtils.isFile(resource) && !ResourceUtils.isPackage(resource)) {
      double lines = 0.0;
      Measure duplicated = context.getMeasure(TrackerMetrics.USELESS_DUPLICATED_LINES);

      if (duplicated != null ) {
        lines += MeasureUtils.getValue(duplicated, 0.0);
        context.saveMeasure(TrackerMetrics.TOTAL_USELESS_LINES, lines);
      }
    }
  }
  
  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }
}
