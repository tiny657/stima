package com.it.model;

import java.util.Set;

public class AllServer {
    public static AllServer instance = new AllServer();
    private Categories categories = new Categories();
    private ServerInfo serverInfo = new ServerInfo();

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
    
    public void removeCategory(String categoryName) {
        categories.remove(categoryName);
    }

    public void addServer(String categoryName, Server server) {
        categories.add(categoryName, server);
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

    public Set<Server> getServers(String category) {
        return categories.getServerListIn(category).getServers();
    }

    public Server getServer(String host, int port) {
        for (String categoryName : categories.getCategoryNames()) {
            Server server = categories.getServerListIn(categoryName).findServer(host, port);
            if (server != null) {
                return server;
            }
        }

        return null;
    }

    public Server getRandomServer(String category) {
        return categories.getServerListIn(category).randomRunningServer();
    }

    public void setStatus(String host, int port, boolean isRunning) {
        categories.setStatus(host, port, isRunning);
    }
    
    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    public String toString() {
        return categories.toString();
    }
}