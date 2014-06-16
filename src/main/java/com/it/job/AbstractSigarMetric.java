package com.it.job;

import org.hyperic.sigar.Sigar;

abstract class AbstractSigarMetric {
    protected final Sigar sigar;

    protected AbstractSigarMetric(Sigar sigar) {
        this.sigar = sigar;
    }
}
