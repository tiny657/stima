package com.it.command;

import java.io.Serializable;

public class Command implements Serializable {
    private static final long serialVersionUID = 8171910229844694018L;

    private String srcHost;
    private int srcPort;

    public Command() {
    }

    public Command(String srcHost, int srcPort) {
        this.srcHost = srcHost;
        this.srcPort = srcPort;
    }

    public String getSrcHost() {
        return srcHost;
    }

    public void setSrcHost(String host) {
        this.srcHost = host;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    @Override
    public String toString() {
        return srcHost + ":" + srcPort;
    }
}