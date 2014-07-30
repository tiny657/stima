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

import com.google.common.base.Preconditions;

public class Latch implements Comparable<Latch> {
  private Status status = Status.DOWN;
  private int threshold, upCount, downCount;
  private int consecutiveCount = 0;
  private boolean statusChanged = false;

  public Latch(int threshold, int upCount, int downCount) {
    Preconditions.checkArgument(upCount > 0);
    Preconditions.checkArgument(downCount > 0);
    this.threshold = threshold;
    this.upCount = upCount;
    this.downCount = downCount;
  }

  public void setValue(int value) {
    if (isOppositeValue(value)) {
      consecutiveCount++;
      checkStatus();
    } else {
      consecutiveCount = 0;
      statusChanged = false;
    }
  }

  public int getThreshold() {
    return threshold;
  }

  public boolean isStatusChanged() {
    return statusChanged;
  }

  public Status getStatus() {
    return status;
  }

  private boolean isOppositeValue(int value) {
    switch (status) {
      case UP:
        if (isDown(value)) {
          return true;
        }
        break;
      case DOWN:
        if (isUp(value)) {
          return true;
        }
        break;
    }

    return false;
  }

  private boolean isDown(int value) {
    return !isUp(value);
  }

  private boolean isUp(int value) {
    if (value < threshold) {
      return false;
    }
    return true;
  }

  private void checkStatus() {
    switch (status) {
      case UP:
        if (consecutiveCount == downCount) {
          setStatus(Status.DOWN);
          return;
        }
        break;

      case DOWN:
        if (consecutiveCount == upCount) {
          setStatus(Status.UP);
          return;
        }
        break;
    }
    statusChanged = false;
  }

  private void setStatus(Status status) {
    if (this.status == status) {
      return;
    }

    this.status = status;
    this.statusChanged = true;
    consecutiveCount = 0;
  }

  @Override
  public int compareTo(Latch o) {
    return o.threshold - threshold;
  }

  enum Status {
    UP, DOWN;
  }
}
