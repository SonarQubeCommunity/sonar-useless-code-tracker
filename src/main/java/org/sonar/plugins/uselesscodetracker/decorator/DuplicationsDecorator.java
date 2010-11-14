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

import org.sonar.api.batch.*;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.*;
import org.sonar.plugins.uselesscodetracker.TrackerMetrics;

import java.util.Arrays;
import java.util.List;

public class DuplicationsDecorator implements Decorator {

  @DependsUpon
  public List<Metric> dependsUpon() {
    return Arrays.asList(TrackerMetrics.TEMP_USELESS_DUPLICATED_LINES);
  }

  @DependedUpon
  public List<Metric> dependedUpon() {
    return Arrays.asList(TrackerMetrics.USELESS_DUPLICATED_LINES);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    if (ResourceUtils.isFile(resource) || ResourceUtils.isPackage(resource)) {
      return;
    }

    if (ResourceUtils.isRootProject(resource) || ResourceUtils.isModuleProject(resource)) {
      Measure projectDuplicatedLines = context.getMeasure(TrackerMetrics.TEMP_USELESS_DUPLICATED_LINES);
      Double childrenDuplicatedLines = MeasureUtils.sum(false, context.getChildrenMeasures(TrackerMetrics.TEMP_USELESS_DUPLICATED_LINES));

      if (projectDuplicatedLines != null || childrenDuplicatedLines != null) {
        double duplicatedLines = MeasureUtils.getValue(projectDuplicatedLines, 0.0);
        if (childrenDuplicatedLines != null) {
          duplicatedLines += childrenDuplicatedLines;
        }
        context.saveMeasure(TrackerMetrics.USELESS_DUPLICATED_LINES, duplicatedLines);
      }
    }
    else {
      Double childrenDuplicatedLines = MeasureUtils.sum(false, context.getChildrenMeasures(TrackerMetrics.USELESS_DUPLICATED_LINES));
      if (childrenDuplicatedLines != null) {
        context.saveMeasure(TrackerMetrics.USELESS_DUPLICATED_LINES, childrenDuplicatedLines);
      }
    }
  }

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }
}