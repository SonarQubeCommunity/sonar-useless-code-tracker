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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.database.model.ResourceModel;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.uselesscodetracker.TrackerMetrics;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Calculates number of duplicated lines that could be reduced.
 * Strategy is following: first block inside first occurrence of group survives, others can be removed.
 * Should be noted that implemented algorithm depends on order of traversal of resources
 * and on order of blocks within group.
 */
public class DuplicationsDecorator implements Decorator {

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  @DependedUpon
  public List<Metric> dependedUpon() {
    return Arrays.asList(TrackerMetrics.USELESS_DUPLICATED_LINES);
  }

  private Set<String> processedResources = Sets.newHashSet();

  public void decorate(Resource resource, DecoratorContext context) {
    double uselessDuplicatedLines = 0;

    Measure measure = context.getMeasure(CoreMetrics.DUPLICATIONS_DATA);
    if (MeasureUtils.hasData(measure)) {
      String resourceKey = new StringBuilder(ResourceModel.KEY_SIZE)
          .append(context.getProject().getKey())
          .append(':')
          .append(context.getResource().getKey())
          .toString();
      List<List<Block>> groups = parseDuplicationData(measure.getData());
      uselessDuplicatedLines = analyse(groups, resourceKey);
    }

    uselessDuplicatedLines += MeasureUtils.sum(true, context.getChildrenMeasures(TrackerMetrics.USELESS_DUPLICATED_LINES));
    if (uselessDuplicatedLines > 0) {
      context.saveMeasure(TrackerMetrics.USELESS_DUPLICATED_LINES, uselessDuplicatedLines);
    }
  }

  /**
   * @return number of duplicated lines that could be reduced
   */
  @VisibleForTesting
  int analyse(List<List<Block>> groups, String currentResourceKey) {
    Set<Integer> linesToRemove = Sets.newHashSet();
    int result = 0;
    for (List<Block> group : groups) {
      result += count(group, currentResourceKey, linesToRemove, isFirstOccurrence(group));
    }
    processedResources.add(currentResourceKey);
    return result;
  }

  /**
   * @param first true if this is a first occurrence of this group, in this case first block should survive
   */
  private int count(List<Block> group, String currentResourceKey, Set<Integer> linesToRemove, boolean first) {
    int result = 0;
    for (Block block : group) {
      if (currentResourceKey.equals(block.resourceKey)) {
        for (int line = block.s; line <= block.e; line++) {
          if (!linesToRemove.contains(line)) {
            linesToRemove.add(line);
            if (!first) {
              result++;
            }
          }
        }
        first = false;
      }
    }
    return result;
  }

  /**
   * If at least one of resources from this group was processed, then this is not a first occurrence of this group.
   * This is due to the fact that if duplication group (A, B) was detected for resource A,
   * then duplication group (B, A) should be detected for resource B.
   */
  private boolean isFirstOccurrence(List<Block> group) {
    for (Block block : group) {
      if (processedResources.contains(block.resourceKey)) {
        return false;
      }
    }
    return true;
  }

  @VisibleForTesting
  static class Block {
    final String resourceKey;
    final int s;
    final int e;

    public Block(String resourceKey, int s, int e) {
      this.resourceKey = resourceKey;
      this.s = s;
      this.e = e;
    }
  }

  /**
   * Parses data of {@link CoreMetrics#DUPLICATIONS_DATA}.
   */
  @VisibleForTesting
  static List<List<Block>> parseDuplicationData(String data) {
    try {
      ImmutableList.Builder<List<Block>> groups = ImmutableList.builder();
      StringReader reader = new StringReader(data);
      SMInputFactory inputFactory = initStax();
      SMHierarchicCursor rootC = inputFactory.rootElementCursor(reader);
      // <duplications>
      rootC.advance();
      SMInputCursor groupsCursor = rootC.childElementCursor("g");
      while (groupsCursor.getNext() != null) {
        // <g>
        SMInputCursor blocksCursor = groupsCursor.childElementCursor("b");
        ImmutableList.Builder<Block> group = ImmutableList.builder();
        while (blocksCursor.getNext() != null) {
          // <b>
          String resourceKey = blocksCursor.getAttrValue("r");
          int firstLine = getAttrIntValue(blocksCursor, "s");
          int numberOfLines = getAttrIntValue(blocksCursor, "l");

          int lastLine = firstLine + numberOfLines - 1;
          group.add(new Block(resourceKey, firstLine, lastLine));
        }
        groups.add(group.build());
      }
      return groups.build();
    } catch (XMLStreamException e) {
      throw new SonarException(e.getMessage(), e);
    }
  }

  private static int getAttrIntValue(SMInputCursor cursor, String attrName) throws XMLStreamException {
    return cursor.getAttrIntValue(cursor.findAttrIndex(null, attrName));
  }

  private static SMInputFactory initStax() {
    XMLInputFactory xmlFactory = XMLInputFactory2.newInstance();
    return new SMInputFactory(xmlFactory);
  }

}
