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

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.collect.Lists;
import com.it.domain.FilesystemMetrics.FileSystem;
import com.it.domain.NetworkMetrics.Network;

public class ResourceMetrics {
  // CPU
  private byte cpuUserUsedPercent, cpuSysUsedPercent;
  private byte loadAvg1M, loadAvg5M, loadAvg15M;

  // Memory
  private int memUsedMB, memTotalMB;
  private int swapUsedMB, swapTotalMB;

  // FileSystem
  private List<System> fileSystems = Lists.newArrayList();

  public static final class System {
    private String deviceName;
    private int freeSpaceMB, totalSizeMB;
    private int rxKBytes, txKBytes;

    public System(FileSystem fileSystem) {
      this.deviceName = fileSystem.deviceName();
      this.freeSpaceMB = fileSystem.freeSpaceMB();
      this.totalSizeMB = fileSystem.totalSizeMB();
      this.rxKBytes = fileSystem.rxKBytes();
      this.txKBytes = fileSystem.txKBytes();
    }

    public String getDeviceName() {
      return deviceName;
    }

    public int getFreeSpaceMB() {
      return freeSpaceMB;
    }

    public int getTotalSizeMB() {
      return totalSizeMB;
    }

    public int getRxKBytes() {
      return rxKBytes;
    }

    public int getTxKBytes() {
      return txKBytes;
    }
  }

  // Network
  private int networkRxKBytes, networkTxKBytes;

  public ResourceMetrics(CpuMetrics cpuMetrics, MemoryMetrics memoryMetrics,
      List<FileSystem> fileSystems, Network network) {
    CpuMetrics.CpuInfo cpu = cpuMetrics.cpu();
    cpuUserUsedPercent = cpu.userUsedPercent();
    cpuSysUsedPercent = cpu.sysUsedPercent();

    loadAvg1M = cpu.loadAvg1M();
    loadAvg5M = cpu.loadAvg5M();
    loadAvg15M = cpu.loadAvg15M();

    memUsedMB = memoryMetrics.mem().usedMB();
    memTotalMB = memoryMetrics.mem().totalMB();
    swapUsedMB = memoryMetrics.swap().usedMB();
    swapTotalMB = memoryMetrics.swap().totalMB();

    for (FileSystem fileSystem : fileSystems) {
      this.fileSystems.add(new System(fileSystem));
    }

    networkRxKBytes = (int) network.rxKBytes();
    networkTxKBytes = (int) network.txKBytes();
  }

  public byte getCpuUserUsedPercent() {
    return cpuUserUsedPercent;
  }

  public byte getCpuSysUsedPercent() {
    return cpuSysUsedPercent;
  }

  public byte getLoadAvg1M() {
    return loadAvg1M;
  }

  public byte getLoadAvg5M() {
    return loadAvg5M;
  }

  public byte getLoadAvg15M() {
    return loadAvg15M;
  }

  public int getNetworkRxKBytes() {
    return networkRxKBytes;
  }

  public int getNetworkTxKBytes() {
    return networkTxKBytes;
  }

  public int getMemUsedMB() {
    return memUsedMB;
  }

  public int getMemTotalMB() {
    return memTotalMB;
  }

  public int getSwapUsedMB() {
    return swapUsedMB;
  }

  public int getSwapTotalMB() {
    return swapTotalMB;
  }

  public List<System> getFileSystems() {
    return fileSystems;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
