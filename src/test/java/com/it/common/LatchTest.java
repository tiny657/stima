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

import org.junit.Test;

public class LatchTest {

  @Test(expected = IllegalArgumentException.class)
  public void latchException1() {
    // When
    new Latch(30, 0, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void latchException2() {
    // When
    new Latch(30, 3, 0);
  }

  @Test
  public void changeStatusUp1() throws Exception {
    // Given
    Latch latch = new Latch(30, 1, 3);

    // When
    latch.setValue(10);
    latch.setValue(40);

    // Then
    assertThat(latch.isStatusChanged(), is(true));
  }

  @Test
  public void changeStatusUp2() throws Exception {
    // Given
    Latch latch = new Latch(30, 1, 3);

    // When
    latch.setValue(10);
    latch.setValue(40);
    latch.setValue(10);
    latch.setValue(10);

    // Then
    assertThat(latch.isStatusChanged(), is(false));
  }

  @Test
  public void changeStatusUp3() throws Exception {
    // Given
    Latch latch = new Latch(30, 1, 3);

    // When
    latch.setValue(10);
    latch.setValue(40);
    latch.setValue(10);
    latch.setValue(10);
    latch.setValue(40);
    latch.setValue(10);

    // Then
    assertThat(latch.isStatusChanged(), is(false));
  }

  @Test
  public void changeStatusDown1() throws Exception {
    // Given
    Latch latch = new Latch(30, 1, 3);

    // When
    latch.setValue(10);
    latch.setValue(40);
    latch.setValue(10);
    latch.setValue(10);
    latch.setValue(10);

    // Then
    assertThat(latch.isStatusChanged(), is(true));
  }

  @Test
  public void changeStatusDown2() throws Exception {
    // Given
    Latch latch = new Latch(30, 1, 3);

    // When
    latch.setValue(10);
    latch.setValue(40);
    latch.setValue(10);
    latch.setValue(10);
    latch.setValue(10);
    latch.setValue(10);

    // Then
    assertThat(latch.isStatusChanged(), is(false));
  }

  @Test
  public void changeStatusDownAndUp1() throws Exception {
    // Given
    Latch latch = new Latch(30, 1, 3);

    // When
    latch.setValue(10);
    latch.setValue(40);
    latch.setValue(10);
    latch.setValue(10);
    latch.setValue(10);
    latch.setValue(40);

    // Then
    assertThat(latch.isStatusChanged(), is(true));
  }
}
