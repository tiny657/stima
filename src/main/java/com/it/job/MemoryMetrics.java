package com.it.job;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;

public class MemoryMetrics extends AbstractSigarMetric {
    protected MemoryMetrics(Sigar sigar) {
        super(sigar);
    }

    public static abstract class MemSegment {
        protected final int total;

        private MemSegment(long total) {
            this.total = (int) (total / 1024L / 1024L);
        }

        public int total() {
            return total;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    public static final class MainMemory extends MemSegment {
        private final int actualUsed;
        private final double usedPercent;

        private MainMemory(long total, long actualUsed, double usedPercent) {
            super(total);
            this.actualUsed = (int) (actualUsed / 1024L / 1024L);
            this.usedPercent = usedPercent;
        }

        public static MainMemory fromSigarBean(Mem mem) {
            return new MainMemory(mem.getTotal(), mem.getActualUsed(),
                    mem.getUsedPercent());
        }

        private static MainMemory undef() {
            return new MainMemory(-1L, -1L, -1L);
        }

        public int actualUsed() {
            return actualUsed;
        }

        public double usedPercent() {
            return usedPercent;
        }
    }

    public static final class SwapSpace extends MemSegment {
        private final int used;

        private SwapSpace(long total, long used) {
            super(total);
            this.used = (int) (used / 1024L / 1024L);
        }

        public static SwapSpace fromSigarBean(Swap swap) {
            return new SwapSpace(swap.getTotal(), swap.getUsed());
        }

        private static SwapSpace undef() {
            return new SwapSpace(-1L, -1L);
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
