package com.it.job;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.google.common.collect.Lists;

public class NetworkMetrics extends AbstractSigarMetric {
    protected NetworkMetrics(Sigar sigar) {
        super(sigar);
    }

    public static final class Network {
        private long rxBytes;
        private long txBytes;

        public Network() {
        }

        public Network(long rxBytes, long txBytes) {
            this.rxBytes = rxBytes;
            this.txBytes = txBytes;
        }

        public static Network fromSigarBean(NetInterfaceStat netInterfaceStat) {
            return new Network(netInterfaceStat.getRxBytes(),
                    netInterfaceStat.getTxBytes());
        }

        public void add(Network network) {
            rxBytes += network.rxBytes;
            txBytes += network.txBytes;
        }

        public Network diff(Network network) {
            return new Network(rxBytes - network.rxBytes(), txBytes
                    - network.txBytes());
        }

        public long rxBytes() {
            return rxBytes;
        }

        public long txBytes() {
            return txBytes;
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
                    NetInterfaceStat netInterfaceStat = sigar
                            .getNetInterfaceStat(each);
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