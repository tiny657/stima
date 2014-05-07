package com.it.model;

import org.apache.commons.lang3.StringUtils;

public class Server implements Comparable<Server> {
    private String host;
    private int port;
    private boolean isRunning = false;

    public Server() {
    }

    public Server(String host, String port) {
        this(host, Integer.valueOf(port));
    }

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = Integer.valueOf(port);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean equals(Server server) {
        return equals(server.host, server.port);
    }

    public boolean equals(String host, int port) {
        if (StringUtils.equals(this.host, host) && this.port == port) {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(Server server) {
        if (host.compareTo(server.getHost()) == 0) {
            return port - server.getPort();
        } else {
            return host.compareTo(server.getHost());
        }
    }

    @Override
    public String toString() {
        return host + ":" + port + "("
                + (isRunning == true ? "running" : "standby") + ")";
    }
}