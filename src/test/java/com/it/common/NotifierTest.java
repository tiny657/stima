/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.it.common;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

public class NotifierTest {
  @Test
  public void thresholdOlder() {
    // Given
    Notifier notifier = new Notifier();

    // When
    notifier.createLatch(Consts.CPU, 3, 1, 3);
    notifier.createLatch(Consts.CPU, 1, 1, 3);
    notifier.createLatch(Consts.CPU, 2, 1, 3);

    // Then
    Collection<Latch> latches = notifier.getLatchMap().get(Consts.CPU);
    assertThat(((Latch) CollectionUtils.get(latches, 0)).getThreshold(), is(3));
    assertThat(((Latch) CollectionUtils.get(latches, 1)).getThreshold(), is(2));
    assertThat(((Latch) CollectionUtils.get(latches, 2)).getThreshold(), is(1));
  }

  @Test
  public void setValue() {
    // Given
    Notifier notifier = new Notifier();
    notifier.createLatch(Consts.CPU, 50, 1, 3);
    notifier.createLatch(Consts.CPU, 30, 1, 3);
    notifier.createLatch(Consts.CPU, 40, 1, 3);

    // When
    notifier.setValue(Consts.CPU, 45);

    // Then
    Collection<Latch> latches = notifier.getLatchMap().get(Consts.CPU);
    assertThat(((Latch) CollectionUtils.get(latches, 0)).getThreshold(), is(50));
    assertThat(((Latch) CollectionUtils.get(latches, 0)).isStatusChanged(), is(false));
    assertThat(((Latch) CollectionUtils.get(latches, 1)).getThreshold(), is(40));
    assertThat(((Latch) CollectionUtils.get(latches, 1)).isStatusChanged(), is(true));
    assertThat(((Latch) CollectionUtils.get(latches, 2)).getThreshold(), is(30));
    assertThat(((Latch) CollectionUtils.get(latches, 2)).isStatusChanged(), is(true));
  }
}
