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
