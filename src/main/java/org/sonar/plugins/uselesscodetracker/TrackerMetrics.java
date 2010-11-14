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

package org.sonar.plugins.uselesscodetracker;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.Arrays;
import java.util.List;

public class TrackerMetrics implements Metrics {

  public static final Metric USELESS_DUPLICATED_LINES = new Metric("useless-duplicated-lines", "Useless Duplicated Lines",
    "Number of duplicated lines that could be reduced", Metric.ValueType.INT, Metric.DIRECTION_WORST, false,
    CoreMetrics.DOMAIN_DUPLICATION);

  public static final Metric TEMP_USELESS_DUPLICATED_LINES = new Metric("temp_useless-duplicated-lines", "Temporary Useless Duplicated Lines",
    "This metric is used to store the results of CPD for each Maven modules. The real metric to store and display final values is " +
      "USELESS_DUPLICATED_LINES.", Metric.ValueType.INT, Metric.DIRECTION_WORST, false,
    CoreMetrics.DOMAIN_DUPLICATION).setHidden(true);

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
      TEMP_USELESS_DUPLICATED_LINES,
      TOTAL_USELESS_LINES,
      DEAD_CODE,
      POTENTIAL_DEAD_CODE);
  }
}
