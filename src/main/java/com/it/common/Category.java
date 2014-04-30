package com.it.common;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

public class Category {
    private List<Server> servers = Lists.newArrayList();
    private List<Server> runningServers = Lists.newArrayList();
    private final Random random = new Random();

    public List<Server> getServers() {
        return servers;
    }

    public Server randomRunningServer() {
        return servers.get(random.nextInt(runningServers.size()));
    }
    
    public Server findServer(String host, int port) {
        for (Server server : servers) {
            if (server.equals(host, port)) {
                return server;
            }
        }

        return null;
    }

    public void addServer(Server server) {
        servers.add(server);
    }

    public void addRunningServer(Server server) {
        runningServers.add(server);
    }

    public void removeRunningServer(Server server) {
        runningServers.remove(server);
    }

    @Override
    public String toString() {
        return servers.toString();
    }
}