package com.it.model;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;

public class Categories {
    private Map<String, ServerList> serverListMap = Maps.newHashMap();

    public void add(String categoryName) {
        if (!serverListMap.containsKey(categoryName)) {
            serverListMap.put(categoryName, new ServerList());
        }
    }

    public void remove(String categoryName) {
        if (serverListMap.containsKey(categoryName)) {
            serverListMap.remove(categoryName);
        }
    }

    public void add(String categoryName, Server server) {
        serverListMap.get(categoryName).addServer(server);
    }

    public void remove(String categoryName, Server server) {
        serverListMap.get(categoryName).removeServer(server);
    }

    public Map<String, ServerList> getServerListMap() {
        return serverListMap;
    }

    public ServerList getCategory(String category) {
        return serverListMap.get(category);
    }

    public Set<Server> getServers(String category) {
        return serverListMap.get(category).getServers();
    }

    public Server getRandomServer(String category) {
        return serverListMap.get(category).randomRunningServer();
    }

    public void setStatus(String host, int port, boolean isRunning) {
        for (Entry<String, ServerList> entry : serverListMap.entrySet()) {
            ServerList serverList = entry.getValue();
            if (serverList.setStatus(host, port, isRunning)) {
                break;
            }
        }
    }

    public boolean equals(Categories categories) {
        if (serverListMap.size() != categories.getServerListMap().size()) {
            return false;
        }

        for (String category : serverListMap.keySet()) {
            if (serverListMap.get(category).getServers().size() != categories
                    .getServerListMap().get(category).getServers().size()) {
                return false;
            }

            Iterator<Server> iterator = serverListMap.get(category)
                    .getServers().iterator();
            Iterator<Server> iterator2 = categories.getServerListMap()
                    .get(category).getServers().iterator();
            while (iterator.hasNext()) {
                Server server = iterator.next();
                Server server2 = iterator2.next();
                if (!server.equals(server2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<String, ServerList> diff(Categories categories) {
        Map<String, ServerList> result = Maps.newHashMap();
        for (String category : serverListMap.keySet()) {
            ServerList serverList = serverListMap.get(category);
            if (serverList != null) {
                result.put(
                        category,
                        serverList.diff(categories.getServerListMap().get(
                                category)));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer("\n* server list\n");
        for (String category : serverListMap.keySet()) {
            result.append("category: ").append(category).append(", servers: ")
                    .append(serverListMap.get(category).toString())
                    .append("\n");
        }

        return result.toString();
    }
}