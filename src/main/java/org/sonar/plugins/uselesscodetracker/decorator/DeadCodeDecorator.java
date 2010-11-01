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

import org.apache.commons.lang.StringUtils;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.*;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.uselesscodetracker.TrackerMetrics;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceMethod;
import org.sonar.squid.indexer.QueryByParent;
import org.sonar.squid.indexer.QueryByType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DeadCodeDecorator implements Decorator {

  @DependedUpon
  public List<Metric> dependedUpon() {
    return Arrays.asList(TrackerMetrics.DEAD_CODE, TrackerMetrics.POTENTIAL_DEAD_CODE);
  }

  public void decorate(Resource resource, DecoratorContext context) {

    if (!Resource.QUALIFIER_CLASS.equals(resource.getQualifier())) {
      for (Metric metric : dependedUpon()) {
        Double measure = MeasureUtils.sum(false, context.getChildrenMeasures(metric));

        if (measure != null) {
          context.saveMeasure(metric, measure);
        }
      }
    }
  }


  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

}