package com.it.job;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class CpuMetrics extends AbstractSigarMetric {
    protected CpuMetrics(Sigar sigar) {
        super(sigar);
    }

    public static final class CpuInfo {
        private final long idle;
        private final long total;
        private final double cpuUsedPercentage;
        private final double[] loadAverages;

        public CpuInfo(long idle, long total, double cpuUsedPercentage,
                double[] loadAverages) {
            this.idle = idle;
            this.total = total;
            this.cpuUsedPercentage = cpuUsedPercentage;
            this.loadAverages = loadAverages;
        }

        public static CpuInfo fromSigarBean(Cpu cpu, CpuPerc cpuPerc,
                double[] loadAverages) {
            return new CpuInfo(cpu.getIdle(), cpu.getTotal(),
                    cpuPerc.getCombined() * 100, loadAverages);
        }

        public long idle() {
            return idle;
        }

        public long total() {
            return total;
        }

        public double cpuUsedPercentage() {
            return cpuUsedPercentage;
        }

        public double[] loadAverages() {
            return loadAverages;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    public CpuInfo cpu() {
        try {
            return CpuInfo.fromSigarBean(sigar.getCpu(), sigar.getCpuPerc(),
                    sigar.getLoadAverage());
        } catch (SigarException e) {
            e.printStackTrace();
        }
        return null;
    }
}
