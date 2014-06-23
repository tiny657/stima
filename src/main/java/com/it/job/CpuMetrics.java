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
        private final byte userUsedPercentage;
        private final byte sysUsedPercentage;
        private final byte loadAverage1M;
        private final byte loadAverage5M;
        private final byte loadAverage15M;

        public CpuInfo(double userUsedPercentage, double sysUsedPercentage,
                double[] loadAverages) {
            this.userUsedPercentage = (byte) (userUsedPercentage + 0.5);
            this.sysUsedPercentage = (byte) (sysUsedPercentage + 0.5);
            this.loadAverage1M = (byte) loadAverages[0];
            this.loadAverage5M = (byte) loadAverages[1];
            this.loadAverage15M = (byte) loadAverages[2];
        }

        public static CpuInfo fromSigarBean(CpuPerc cpuPerc,
                double[] loadAverages) {
            return new CpuInfo(cpuPerc.getUser() * 100, cpuPerc.getSys() * 100,
                    loadAverages);
        }

        public static CpuInfo undef() {
            return new CpuInfo(-1, -1, new double[] { -1, -1, -1 });
        }

        public byte userUsedPercentage() {
            return userUsedPercentage;
        }

        public byte sysUsedPercentage() {
            return sysUsedPercentage;
        }

        public byte loadAverage1M() {
            return loadAverage1M;
        }

        public byte loadAverage5M() {
            return loadAverage5M;
        }

        public byte loadAverage15M() {
            return loadAverage15M;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    public CpuInfo cpu() {
        try {
            return CpuInfo.fromSigarBean(sigar.getCpuPerc(),
                    sigar.getLoadAverage());
        } catch (SigarException e) {
            e.printStackTrace();
        }
        return CpuInfo.undef();
    }
}
