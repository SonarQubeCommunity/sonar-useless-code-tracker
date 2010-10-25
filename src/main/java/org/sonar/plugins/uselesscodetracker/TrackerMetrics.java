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
package org.sonar.plugins.uselesscodetracker;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;
import org.sonar.api.measures.SumChildValuesFormula;
import org.sonar.squid.measures.SumAggregationFormula;

import java.util.Arrays;
import java.util.List;

public class TrackerMetrics implements Metrics {

  public static final Metric USELESS_DUPLICATED_LINES = new Metric("useless-duplicated-lines", "Useless Duplicated Lines",
    "Number of duplicated lines that could be reduced", Metric.ValueType.INT, Metric.DIRECTION_WORST, false,
    CoreMetrics.DOMAIN_DUPLICATION).setFormula(new SumChildValuesFormula(false));

  public static final Metric DEAD_CODE = new Metric("dead-code", "Dead Code",
    "Code that is not used and could be removed", Metric.ValueType.INT, Metric.DIRECTION_WORST, false,
    CoreMetrics.DOMAIN_RULES);

  public static final Metric POTENTIAL_DEAD_CODE = new Metric("potential-dead-code", "Potential Dead Code",
    "Code that is potentially not used and could be removed", Metric.ValueType.INT, Metric.DIRECTION_WORST, false,
    CoreMetrics.DOMAIN_RULES);

  public static final Metric TOTAL_USELESS_LINES = new Metric("total-useless-lines", "Total Useless Code",
    "Number of lines that can be reduced", Metric.ValueType.INT, Metric.DIRECTION_WORST, false, CoreMetrics.DOMAIN_SIZE);

  public List<Metric> getMetrics() {
    return Arrays.asList(
      USELESS_DUPLICATED_LINES,
      TOTAL_USELESS_LINES,
      DEAD_CODE,
      POTENTIAL_DEAD_CODE);
  }
}
