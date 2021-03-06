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

package com.it.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;

public class MemoryMetrics extends AbstractSigarMetric {
  public MemoryMetrics(Sigar sigar) {
    super(sigar);
  }

  public static abstract class MemSegment {
    protected final int totalMB;

    private MemSegment(long totalBytes) {
      this.totalMB = (int) (totalBytes / 1024L / 1024L);
    }

    public int totalMB() {
      return totalMB;
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this);
    }
  }


  public static final class MainMemory extends MemSegment {
    private final int usedMB;
    private final double usedPercent;

    private MainMemory(long total, long usedBytes, double usedPercent) {
      super(total);
      this.usedMB = (int) (usedBytes / 1024L / 1024L);
      this.usedPercent = usedPercent;
    }

    public static MainMemory fromSigarBean(Mem mem) {
      return new MainMemory(mem.getTotal(), mem.getActualUsed(), mem.getUsedPercent());
    }

    private static MainMemory undef() {
      return new MainMemory(-1L, -1L, -1L);
    }

    public int usedMB() {
      return usedMB;
    }

    public double usedPercent() {
      return usedPercent;
    }
  }


  public static final class SwapSpace extends MemSegment {
    private final int usedMB;

    private SwapSpace(long total, long used) {
      super(total);
      this.usedMB = (int) (used / 1024L / 1024L);
    }

    public static SwapSpace fromSigarBean(Swap swap) {
      return new SwapSpace(swap.getTotal(), swap.getUsed());
    }

    private static SwapSpace undef() {
      return new SwapSpace(-1L, -1L);
    }

    public int usedMB() {
      return usedMB;
    }
  }

  public MainMemory mem() {
    try {
      return MainMemory.fromSigarBean(sigar.getMem());
    } catch (SigarException e) {
      return MainMemory.undef();
    }
  }

  public SwapSpace swap() {
    try {
      return SwapSpace.fromSigarBean(sigar.getSwap());
    } catch (SigarException e) {
      return SwapSpace.undef();
    }
  }
}
