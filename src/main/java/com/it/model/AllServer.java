package com.it.model;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class AllServer {
    public static AllServer instance = new AllServer();
    private Map<String, Category> categories = Maps.newHashMap();
    private List<Server> servers = Lists.newArrayList();

    public static AllServer getInstance() {
        return instance;
    }

    public void addCategory(String categoryName) {
        categories.put(categoryName, new Category());
    }

    public void addServer(String categoryName, Server server) {
        categories.get(categoryName).addServer(server);
        servers.add(server);
    }

    public Map<String, Category> getCategories() {
        return categories;
    }

    public Category getCategory(String category) {
        return categories.get(category);
    }

    public List<Server> getServers() {
        return servers;
    }

    public List<Server> getServers(String category) {
        return categories.get(category).getServers();
    }
    
    public Server getServer(String host, int port) {
        for (Server server : servers) {
            if (server.equals(host, port)) {
                return server;
            }
        }
        return null;
    }

    public Server getRandomServer(String category) {
        return categories.get(category).randomRunningServer();
    }

    public void setStatus(String host, int port, boolean isRunning) {
        for (Entry<String, Category> entry : categories.entrySet()) {
            Server server = entry.getValue().findServer(host, port);
            if (server != null) {
                boolean oldRunning = server.isRunning();
                server.setRunning(isRunning);
                if (oldRunning == false && isRunning == true) {
                    entry.getValue().addRunningServer(server);
                } else if (oldRunning == true && isRunning == false) {
                    entry.getValue().removeRunningServer(server);
                }
                break;
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer("show servers\n");
        for (String category : categories.keySet()) {
            result.append(category).append(categories.get(category).toString())
                    .append("\n");
        }

        return result.toString();
    }
}