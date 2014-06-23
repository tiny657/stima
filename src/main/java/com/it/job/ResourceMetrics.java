package com.it.job;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.collect.Lists;
import com.it.job.CpuMetrics.CpuInfo;
import com.it.job.FilesystemMetrics.FileSystem;
import com.it.job.NetworkMetrics.Network;

public class ResourceMetrics {
    // CPU
    private byte cpuUserUsedPercentage, cpuSysUsedPercentage;
    private byte loadAverage1M, loadAverage5M, loadAverage15M;

    // Memory
    private int memoryUsedMB, memoryTotalMB;
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

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    // Network
    private int networkRxKBytes, networkTxKBytes;

    public ResourceMetrics(CpuMetrics cpuMetrics, MemoryMetrics memoryMetrics,
            List<FileSystem> fileSystems, Network network) {
        CpuInfo cpu = cpuMetrics.cpu();
        cpuUserUsedPercentage = cpu.userUsedPercentage();
        cpuSysUsedPercentage = cpu.sysUsedPercentage();

        loadAverage1M = cpu.loadAverage1M();
        loadAverage5M = cpu.loadAverage5M();
        loadAverage15M = cpu.loadAverage15M();

        memoryUsedMB = memoryMetrics.mem().usedMB();
        memoryTotalMB = memoryMetrics.mem().totalMB();
        swapUsedMB = memoryMetrics.swap().usedMB();
        swapTotalMB = memoryMetrics.swap().totalMB();

        for (FileSystem fileSystem : fileSystems) {
            this.fileSystems.add(new System(fileSystem));
        }

        networkRxKBytes = (int) network.rxKBytes();
        networkTxKBytes = (int) network.txKBytes();
    }

    public float getCpuUserUsedPercentage() {
        return cpuUserUsedPercentage;
    }

    public float getCpuSysUsedPercentage() {
        return cpuSysUsedPercentage;
    }

    public byte getLoadAverage1M() {
        return loadAverage1M;
    }

    public byte getLoadAverage5M() {
        return loadAverage5M;
    }

    public byte getLoadAverage15M() {
        return loadAverage15M;
    }

    public int getNetworkRxKBytesPerSecond() {
        return networkRxKBytes;
    }

    public int getNetworkTxKBytesPerSecond() {
        return networkTxKBytes;
    }

    public int getMemoryUsedMB() {
        return memoryUsedMB;
    }

    public int getMemoryTotalMB() {
        return memoryTotalMB;
    }

    public int getSwapUsedMB() {
        return swapUsedMB;
    }

    public int getSwapTotalMB() {
        return swapTotalMB;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}