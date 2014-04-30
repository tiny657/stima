package com.it.common;

public class Server {
    private String host;
    private int port;
    private boolean isRunning = false;

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

    public String toString() {
        return host + ":" + port + "(status:" + isRunning + ")";
    }
}