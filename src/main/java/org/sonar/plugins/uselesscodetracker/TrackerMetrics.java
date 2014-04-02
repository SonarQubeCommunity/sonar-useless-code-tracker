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

  public static final Metric USELESS_DUPLICATED_LINES = new Metric.Builder("useless-duplicated-lines", "Useless Duplicated Lines", Metric.ValueType.INT)
      .setDescription("Number of duplicated lines that could be reduced")
      .setDirection(Metric.DIRECTION_WORST)
      .setQualitative(false)
      .setBestValue(0.0)
      .setOptimizedBestValue(true)
      .setDomain(CoreMetrics.DOMAIN_DUPLICATION)
      .create();

  public static final Metric TOTAL_USELESS_LINES = new Metric.Builder("total-useless-lines", "Total Useless Code", Metric.ValueType.INT)
      .setDescription("Number of lines that can be reduced")
      .setDirection(Metric.DIRECTION_WORST)
      .setQualitative(false)
      .setBestValue(0.0)
      .setOptimizedBestValue(true)
      .setDomain(CoreMetrics.DOMAIN_SIZE)
      .create();

  public static final Metric TEMP_METHOD_LINES = new Metric.Builder("temp-method-lines", "Temp info on method lines", Metric.ValueType.DATA)
      .setDescription("")
      .setQualitative(false)
      .setDomain(CoreMetrics.DOMAIN_SIZE)
      .create();

  public List<Metric> getMetrics() {
    return Arrays.asList(
        USELESS_DUPLICATED_LINES,
        TOTAL_USELESS_LINES,
        TEMP_METHOD_LINES);
  }

}
