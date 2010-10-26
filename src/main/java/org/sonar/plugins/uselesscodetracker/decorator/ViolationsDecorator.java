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
import org.sonar.api.measures.*;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.*;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.uselesscodetracker.TrackerMetrics;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceMethod;
import org.sonar.squid.indexer.QueryByParent;
import org.sonar.squid.indexer.QueryByType;
import org.sonar.squid.text.Source;

import java.util.*;

@DependsUpon(DecoratorBarriers.END_OF_VIOLATIONS_GENERATION)
public class ViolationsDecorator implements Decorator {
  private SquidSearch squid;
  private RulesProfile rulesProfile;
  private RuleFinder ruleFinder;

  public ViolationsDecorator(SquidSearch squid, RulesProfile rulesProfile, RuleFinder ruleFinder) {
    this.squid = squid;
    this.rulesProfile = rulesProfile;
    this.ruleFinder = ruleFinder;
  }

  @DependedUpon
  public List<Metric> dependsUpon() {
    return Arrays.asList(TrackerMetrics.DEAD_CODE, TrackerMetrics.POTENTIAL_DEAD_CODE);
  }

  public void decorate(Resource resource, DecoratorContext context) {

    if (ResourceUtils.isFile(resource)) {
      List<Rule> deadCodeRules = new ArrayList();
      deadCodeRules.add(ruleFinder.findByKey(CoreProperties.SQUID_PLUGIN, "UnusedPrivateMethod"));
      deadCodeRules.add(ruleFinder.findByKey(CoreProperties.PMD_PLUGIN, "UnusedPrivateMethod"));

      List<Rule> potentialDeadCodeRules = new ArrayList();
      deadCodeRules.add(ruleFinder.findByKey(CoreProperties.SQUID_PLUGIN, "UnusedProtectedMethod"));

      saveMeasure(resource, TrackerMetrics.DEAD_CODE, context, deadCodeRules);
      saveMeasure(resource, TrackerMetrics.POTENTIAL_DEAD_CODE, context, potentialDeadCodeRules);
    } else {
      for (Metric metric : dependsUpon()) {
        Double sum = 0.0;
        if (context.getMeasure(metric) == null) {
          sum += MeasureUtils.sum(true, context.getChildrenMeasures(metric));
          context.saveMeasure(new Measure(metric, sum));
        }
      }
    }
  }

  private void saveMeasure(Resource resource, Metric metric, DecoratorContext context, List<Rule> rules) {
    Double sum = 0.0;

    for (Rule rule : rules) {
      if (rulesProfile.getActiveRule(rule) != null) {
        for (Violation violation : context.getViolations()) {
          if (violation.getRule().equals(rule)) {
            int lineId = violation.getLineId();
            Collection<SourceCode> methods = squid.search(new QueryByParent(squid.search(convertToSquidKeyFormat((JavaFile) resource))), new QueryByType(SourceMethod.class));

            for (SourceCode method : methods) {
              if (method.getStartAtLine() == lineId) {
                sum += (method.getEndAtLine() - method.getStartAtLine() + 1);
              }
            }
          }
        }
      }
    }
    context.saveMeasure(new Measure(metric, sum));
  }

  public boolean shouldExecuteOnProject(Project project) {
    if (Java.INSTANCE.equals(project.getLanguage())) {
      return true;
    }
    return false;
  }

  public static String convertToSquidKeyFormat(JavaFile file) {
    String key = file.getKey();
    if (file.getParent() == null || file.getParent().isDefault()) {
      key = StringUtils.substringAfterLast(file.getKey(), ".");
    } else {
      key = StringUtils.replace(key, ".", "/");
    }
    return key + ".java";
  }
}
