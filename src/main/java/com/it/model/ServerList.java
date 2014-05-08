package com.it.model;

import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ServerList {
    private Set<Server> servers = Sets.newTreeSet();
    private List<Server> runningServers = Lists.newArrayList();
    private final Random random = new Random();

    public ServerList() {
    }

    public ServerList(Set<Server> servers) {
        this.servers.addAll(servers);
    }

    public Set<Server> getServers() {
        return servers;
    }

    public List<Server> getRunningServers() {
        return runningServers;
    }

    public Server randomRunningServer() {
        if (runningServers.size() == 0) {
            return null;
        }

        return runningServers.get(random.nextInt(runningServers.size()));
    }

    public Server findServer(String host, int port) {
        for (Server server : servers) {
            if (server.equals(host, port)) {
                return server;
            }
        }

        return null;
    }

    public boolean contains(Server server) {
        if (findServer(server.getHost(), server.getPort()) == null)
            return false;

        return true;
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

    public void addRunningServer(Server server) {
        runningServers.add(server);
    }

    public void removeServer(Server server) {
        servers.remove(server);
    }

    public void removeRunningServer(Server server) {
        runningServers.remove(server);
    }

    @Override
    public String toString() {
        return servers.toString();
    }
}