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

import com.google.common.collect.Lists;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.*;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.KeyValueFormat;
import org.sonar.plugins.uselesscodetracker.TrackerMetrics;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@DependsUpon(DecoratorBarriers.END_OF_VIOLATIONS_GENERATION)
public class ViolationsDecorator implements Decorator {
  private RulesProfile rulesProfile;
  private RuleFinder ruleFinder;

  public ViolationsDecorator(RulesProfile rulesProfile, RuleFinder ruleFinder) {
    this.rulesProfile = rulesProfile;
    this.ruleFinder = ruleFinder;
  }

  @DependedUpon
  public List<Metric> dependedUpon() {
    return Arrays.asList(TrackerMetrics.DEAD_CODE, TrackerMetrics.POTENTIAL_DEAD_CODE);
  }

  @DependsUpon
  public List<Metric> dependsUpon() {
    return Arrays.asList(TrackerMetrics.TEMP_METHOD_LINES);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    if (ResourceUtils.isClass(resource)) {
      List<Rule> deadCodeRules = Lists.newArrayList();
      deadCodeRules.add(ruleFinder.findByKey(CoreProperties.SQUID_PLUGIN, "UnusedPrivateMethod"));
      deadCodeRules.add(ruleFinder.findByKey(CoreProperties.PMD_PLUGIN, "UnusedPrivateMethod"));

      List<Rule> potentialDeadCodeRules = Lists.newArrayList();
      potentialDeadCodeRules.add(ruleFinder.findByKey(CoreProperties.SQUID_PLUGIN, "UnusedProtectedMethod"));

      saveFileMeasure(TrackerMetrics.DEAD_CODE, context, deadCodeRules);
      saveFileMeasure(TrackerMetrics.POTENTIAL_DEAD_CODE, context, potentialDeadCodeRules);
    }
  }

  private void saveFileMeasure(Metric metric, DecoratorContext context, List<Rule> rules) {
    double sum = 0.0;
    Measure tempLines = context.getMeasure(TrackerMetrics.TEMP_METHOD_LINES);
    String numberOfLines = "";
    if (tempLines != null) {
      numberOfLines = tempLines.getData();
    }
    Map<String, String> methodLines = KeyValueFormat.parse(numberOfLines);
    for (Rule rule : rules) {
      if (rulesProfile.getActiveRule(rule) != null) {
        for (Violation violation : context.getViolations()) {
          if (violation.getRule().equals(rule)) {
            Integer id  = violation.getLineId();
            if (id == null) {
              // means that the violation was not attached. We should skip it
              continue;
            }

            if (methodLines.containsKey(Integer.toString(id))) {
              sum += Integer.valueOf(methodLines.get(Integer.toString(id)));
            }
          }
        }
      }
    }
    context.saveMeasure(new Measure(metric, sum));
  }

  public boolean shouldExecuteOnProject(Project project) {
    return Java.KEY.equals(project.getLanguageKey());
  }
}
