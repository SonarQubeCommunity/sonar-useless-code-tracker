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
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Project;
import org.sonar.plugins.uselesscodetracker.TrackerMetrics;
import org.sonar.plugins.uselesscodetracker.decorator.DuplicationsDecorator.Block;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DuplicationsDecoratorTest {

  private DuplicationsDecorator decorator;

  @Before
  public void setUp() {
    decorator = new DuplicationsDecorator();
  }

  @Test
  public void shouldExecuteOnAnyProject() {
    Project project = new Project("key");
    assertThat(decorator.shouldExecuteOnProject(project), is(true));
  }

  @Test
  public void dependencies() {
    assertThat(decorator.dependedUpon(), hasItem(TrackerMetrics.USELESS_DUPLICATED_LINES));
  }

  @Test
  public void test1() {
    List<List<Block>> groups = Lists.newArrayList();
    groups.add(Arrays.asList(new Block("key1", 5, 6), new Block("key2", 10, 11)));
    int linesToRemove;

    linesToRemove = decorator.analyse(groups, "key1");
    assertThat(linesToRemove, is(0));

    linesToRemove = decorator.analyse(groups, "key2");
    assertThat(linesToRemove, is(2));
  }

  @Test
  public void test2() {
    List<List<Block>> groups = Lists.newArrayList();
    groups.add(Arrays.asList(new Block("key1", 1, 2), new Block("key2", 5, 6), new Block("key2", 10, 11)));
    int linesToRemove;

    linesToRemove = decorator.analyse(groups, "key1");
    assertThat(linesToRemove, is(0));

    linesToRemove = decorator.analyse(groups, "key2");
    assertThat(linesToRemove, is(4));
  }

  @Test
  public void test3() {
    List<List<Block>> groups = Lists.newArrayList();
    groups.add(Arrays.asList(new Block("key1", 5, 6), new Block("key1", 10, 11)));
    int linesToRemove = decorator.analyse(groups, "key1");
    assertThat(linesToRemove, is(2));
  }

  @Test
  public void test4() {
    List<List<Block>> groups = Lists.newArrayList();
    groups.add(Arrays.asList(new Block("key1", 5, 6), new Block("key1", 6, 7)));
    int linesToRemove = decorator.analyse(groups, "key1");
    assertThat(linesToRemove, is(1));
  }

  @Test
  public void shouldParse() {
    String data = "<duplications><g>"
        + "<b s=\"5\" l=\"2\" r=\"key1\"/>"
        + "<b s=\"15\" l=\"2\" r=\"key2\"/>"
        + "</g></duplications>";
    List<List<Block>> groups = DuplicationsDecorator.parseDuplicationData(data);
    assertThat(groups.size(), is(1));
    List<Block> group = groups.get(0);
    assertThat(group.size(), is(2));
    assertThat(group.get(0).resourceKey, is("key1"));
    assertThat(group.get(0).s, is(5));
    assertThat(group.get(0).e, is(6));
    assertThat(group.get(1).resourceKey, is("key2"));
    assertThat(group.get(1).s, is(15));
    assertThat(group.get(1).e, is(16));
  }

  @Test
  public void shouldNotParseOldFormat() {
    String data = "<duplications>"
        + "<duplication start=\"5\" lines=\"2\" target-resource=\"key1\" target-start=\"15\" />"
        + "</duplications>";
    List<List<Block>> groups = DuplicationsDecorator.parseDuplicationData(data);
    assertThat(groups.size(), is(0));
  }

}
