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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class FilesystemMetrics extends AbstractSigarMetric {
  public FilesystemMetrics(Sigar sigar) {
    super(sigar);
  }

  public static final class FileSystem {
    private final String deviceName;
    private final int totalSizeMB;
    private final int freeSpaceMB;
    private final int rxKBytes;
    private final int txKBytes;

    public FileSystem(String deviceName, long totalSizeMB, long freeSpaceMB, long rxKBytes,
        long txKBytes) {
      this.deviceName = deviceName;
      this.totalSizeMB = (int) (totalSizeMB);
      this.freeSpaceMB = (int) (freeSpaceMB);
      this.rxKBytes = (int) (rxKBytes);
      this.txKBytes = (int) (txKBytes);
    }

    public static FileSystem fromSigarBean(org.hyperic.sigar.FileSystem fs, FileSystemUsage usage) {
      return new FileSystem(fs.getDevName(), usage.getTotal() / 1024L, usage.getFree() / 1024L,
          usage.getDiskReadBytes() / 1024L, usage.getDiskWriteBytes() / 1024L);
    }

    public FileSystem diff(FileSystem fileSystem) {
      return new FileSystem(deviceName, totalSizeMB, freeSpaceMB, txKBytes - fileSystem.txKBytes(),
          rxKBytes - fileSystem.rxKBytes());
    }

    public String deviceName() {
      return deviceName;
    }

    public int totalSizeMB() {
      return totalSizeMB;
    }

    public int freeSpaceMB() {
      return freeSpaceMB;
    }

    public int rxKBytes() {
      return rxKBytes;
    }

    public int txKBytes() {
      return txKBytes;
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this);
    }
  }

  public List<FileSystem> fileSystems() {
    List<FileSystem> result = new ArrayList<FileSystem>();
    org.hyperic.sigar.FileSystem[] fss = null;
    try {
      fss = sigar.getFileSystemList();
    } catch (SigarException e) {
      // give up
      return result;
    }

    if (fss == null) {
      return result;
    }

    for (org.hyperic.sigar.FileSystem fs : fss) {
      try {
        FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
        result.add(FileSystem.fromSigarBean(fs, usage));
      } catch (SigarException e) {
        e.printStackTrace();
      }
    }
    return result;
  }
}
