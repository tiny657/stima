package com.it.model;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

public class ServerList implements Serializable {
    private static final long serialVersionUID = 307182214967241367L;

    private List<Server> servers = Lists.newArrayList();
    transient private int index = 0;

    public ServerList() {
    }

    public boolean hasServers() {
        return servers.size() > 0;
    }

    public int size() {
        return servers.size();
    }

    public List<Server> getServers() {
        return servers;
    }

    public List<Server> getRunningServers() {
        List<Server> runningServers = Lists.newArrayList();
        for (Server server : servers) {
            if (server.isRunning()) {
                runningServers.add(server);
            }
        }
        return runningServers;
    }

    public Server nextRunningServer() {
        Server next = null;
        for (int i = 0; i < servers.size(); i++) {
            Server server = servers.get(index);
            if (server.isRunning()) {
                next = server;
                break;
            }
        }
        return next;
    }

    public boolean setStatus(String host, int port, boolean isRunning) {
        Server server = findServer(host, port);
        if (server == null) {
            return false;
        }
        server.setRunning(isRunning);

        return true;
    }

    public boolean contains(Server server) {
        if (findServer(server.getHost(), server.getPort()) == null) {
            return false;
        }

        return true;
    }

    public Server findServer(String host, int port) {
        for (Server server : servers) {
            if (server.equals(host, port)) {
                return server;
            }
        }

        return null;
    }

    public ServerList diff(ServerList serverList) {
        ServerList result = new ServerList();
        for (Server server : getServers()) {
            if (serverList == null || !serverList.contains(server)) {
                result.addServer(server);
            }
        }
        return result;
    }

    public void addServer(Server server) {
        servers.add(server);
    }

    public void removeServer(Server server) {
        servers.remove(server);
    }

    @Override
    public String toString() {
        return servers.toString();
    }
}