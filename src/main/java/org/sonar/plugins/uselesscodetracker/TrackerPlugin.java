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

import com.google.common.collect.ImmutableList;
import org.sonar.api.SonarPlugin;
import org.sonar.plugins.uselesscodetracker.decorator.*;

import java.util.List;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

public class TrackerPlugin extends SonarPlugin {

  public static final String ENABLED_DEFAULT = "true";
  public static final String TRACKER_CATEGORY = "Useless Code Tracker";
  public static final String COMMON_SUBCATEGORY = "General";
  public static final String ENABLED = "sonar.useless-code-tracker.enabled";

  @Override
  public List getExtensions() {

    return ImmutableList.of(
            PropertyDefinition.builder(ENABLED).
            defaultValue(ENABLED_DEFAULT).
            name("Activation of Useless code tracker plugin").
            description("This property can be set to false in order to deactivate the Useless code tracker plugin.").
            index(0).
            onQualifiers(Qualifiers.PROJECT).
            category(TRACKER_CATEGORY).
            subCategory(COMMON_SUBCATEGORY).
            type(PropertyType.BOOLEAN).
            build(),
            TrackerMetrics.class,
            TempMethodLinesDecorator.class,
            TrackerWidget.class,
            TotalDecorator.class,
            DuplicationsDecorator.class);
  }

}
