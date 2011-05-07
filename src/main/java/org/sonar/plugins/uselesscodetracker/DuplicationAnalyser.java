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

import java.util.Iterator;

import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.duplications.cpd.Match;

public class DuplicationAnalyser {

  private SensorContext context;
  private Project project;

  public DuplicationAnalyser(Project project, SensorContext context) {
    this.project = project;
    this.context = context;
  }

  public void analyse(Iterator<Match> matches) {
    double duplicatedLines = 0.0;

    while (matches.hasNext()) {
      Match match = matches.next();
      duplicatedLines += (match.getMarkCount() - 1) * match.getLineCount();
    }

    context.saveMeasure(project, TrackerMetrics.TEMP_USELESS_DUPLICATED_LINES, duplicatedLines);
  }
}
