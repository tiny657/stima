package com.it.model;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.google.common.collect.Lists;

public class NetworkMetrics extends AbstractSigarMetric {
  public NetworkMetrics(Sigar sigar) {
    super(sigar);
  }

  public static final class Network {
    private long rxKBytes;
    private long txKBytes;

    public Network() {}

    public Network(long rxKBytes, long txKBytes) {
      this.rxKBytes = rxKBytes;
      this.txKBytes = txKBytes;
    }

    public static Network fromSigarBean(NetInterfaceStat netInterfaceStat) {
      return new Network(netInterfaceStat.getRxBytes() / 1024L,
          netInterfaceStat.getTxBytes() / 1024L);
    }

    public void add(Network network) {
      rxKBytes += network.rxKBytes;
      txKBytes += network.txKBytes;
    }

    public Network diff(Network network) {
      return new Network(rxKBytes - network.rxKBytes(), txKBytes - network.txKBytes());
    }

    public long rxKBytes() {
      return rxKBytes;
    }

    public long txKBytes() {
      return txKBytes;
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this);
    }
  }

  private List<Network> networks() {
    List<Network> result = Lists.newArrayList();
    try {
      for (String each : sigar.getNetInterfaceList()) {
        try {
          NetInterfaceStat netInterfaceStat = sigar.getNetInterfaceStat(each);
          result.add(Network.fromSigarBean(netInterfaceStat));
        } catch (Exception e) {
        }
      }
    } catch (SigarException e) {
      e.printStackTrace();
    }
    return result;
  }

  public Network sumOfNetworks() {
    Network result = new Network();
    for (Network network : networks()) {
      result.add(network);
    }
    return result;
  }
}
