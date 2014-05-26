package com.it.model;

import java.util.List;

public class AllServer {
    public static AllServer instance = new AllServer();
    private Categories categories = new Categories();
    private ServerInfos serverInfos = new ServerInfos();

    public static AllServer getInstance() {
        return instance;
    }

    public void addCategory(String[] categoryNames) {
        for (String categoryName : categoryNames) {
            addCategory(categoryName);
        }
    }

    public void addCategory(String categoryName) {
        categories.add(categoryName);
    }

    public void addServer(String categoryName, Server server) {
        categories.add(categoryName, server);
    }

    public void removeCategory(String categoryName) {
        categories.remove(categoryName);
    }

    public void removeServer(String categoryName, Server server) {
        categories.remove(categoryName, server);
    }

    public Categories getCategories() {
        return categories;
    }

    public ServerList getServerListIn(String category) {
        return categories.getServerListIn(category);
    }

    public List<Server> getServers(String category) {
        return categories.getServerListIn(category).getServers();
    }

    public Server getServer(Server server) {
        return getServer(server.getHost(), server.getPort());
    }

    public Server getServer(String host, int port) {
        for (String categoryName : categories.getCategoryNames()) {
            Server server = categories.getServerListIn(categoryName)
                    .findServer(host, port);
            if (server != null) {
                return server;
            }
        }

        return null;
    }

    public Server getRandomServer(String category) {
        return categories.getServerListIn(category).nextRunningServer();
    }

    public void setStatus(Server server, boolean isRunning) {
        categories.setStatus(server, isRunning);
    }

    public ServerInfos getServerInfos() {
        return serverInfos;
    }

    @Override
    public String toString() {
        return categories.toString();
    }
}