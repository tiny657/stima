package com.it.domain;

import org.hyperic.sigar.Sigar;

abstract class AbstractSigarMetric {
  protected final Sigar sigar;

  public AbstractSigarMetric(Sigar sigar) {
    this.sigar = sigar;
  }
}
