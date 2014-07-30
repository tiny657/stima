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
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class CpuMetrics extends AbstractSigarMetric {
  public CpuMetrics(Sigar sigar) {
    super(sigar);
  }

  public static final class CpuInfo {
    private final byte userUsedPercent;
    private final byte sysUsedPercent;
    private final byte loadAvg1M;
    private final byte loadAvg5M;
    private final byte loadAvg15M;

    public CpuInfo(double userUsedPercent, double sysUsedPercent, double[] loadAvgs) {
      this.userUsedPercent = (byte) (userUsedPercent + 0.5);
      this.sysUsedPercent = (byte) (sysUsedPercent + 0.5);
      this.loadAvg1M = (byte) loadAvgs[0];
      this.loadAvg5M = (byte) loadAvgs[1];
      this.loadAvg15M = (byte) loadAvgs[2];
    }

    public static CpuInfo fromSigarBean(CpuPerc cpuPerc, double[] loadAvgs) {
      return new CpuInfo(cpuPerc.getUser() * 100, cpuPerc.getSys() * 100, loadAvgs);
    }

    public static CpuInfo undef() {
      return new CpuInfo(-1, -1, new double[] {-1, -1, -1});
    }

    public byte userUsedPercent() {
      return userUsedPercent;
    }

    public byte sysUsedPercent() {
      return sysUsedPercent;
    }

    public byte loadAvg1M() {
      return loadAvg1M;
    }

    public byte loadAvg5M() {
      return loadAvg5M;
    }

    public byte loadAvg15M() {
      return loadAvg15M;
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this);
    }
  }

  public CpuInfo cpu() {
    try {
      return CpuInfo.fromSigarBean(sigar.getCpuPerc(), sigar.getLoadAverage());
    } catch (SigarException e) {
      e.printStackTrace();
    }
    return CpuInfo.undef();
  }
}
