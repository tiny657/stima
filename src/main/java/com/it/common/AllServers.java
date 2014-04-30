package com.it.common;

import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class AllServers {
    public static AllServers instance = new AllServers();
    private Map<String, Category> categories = Maps.newHashMap();
    private List<Server> servers = Lists.newArrayList();

    public static AllServers getInstance() {
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

    public void setStatus(String host, int port, boolean isRunning) {
        for (Server server : servers) {
            if (server.getHost().equals(host) && server.getPort() == port) {
                server.setRunning(isRunning);
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