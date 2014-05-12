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
        serverListMap.remove(categoryName);
    }

    public void add(String categoryName, Server server) {
        ServerList serverList = getServerListIn(categoryName);
        if (serverList == null) {
            add(categoryName);
            serverList = getServerListIn(categoryName);
        }
        serverList.addServer(server);
    }

    public void remove(String categoryName, Server server) {
        ServerList serverList = getServerListIn(categoryName);
        if (serverList != null) {
            serverList.removeServer(server);
        }
    }

    public Set<String> getCategoryNames() {
        return serverListMap.keySet();
    }

    public ServerList getServerListIn(String category) {
        return serverListMap.get(category);
    }

    public Server randomRunningServer(String category) {
        ServerList serverList = getServerListIn(category);
        if (serverList != null) {
            return serverList.randomRunningServer();
        }
        return null;
    }

    public boolean setStatus(String host, int port, boolean isRunning) {
        for (Entry<String, ServerList> entry : serverListMap.entrySet()) {
            ServerList serverList = entry.getValue();
            if (serverList.setStatus(host, port, isRunning)) {
                return true;
            }
        }
        return false;
    }

    public boolean equals(Categories categories) {
        if (serverListMap.size() != categories.getCategoryNames().size()) {
            return false;
        }

        for (String category : getCategoryNames()) {
            if (getServerListIn(category).getServers().size() != categories
                    .getServerListIn(category).getServers().size()) {
                return false;
            }

            Iterator<Server> iterator = getServerListIn(category).getServers()
                    .iterator();
            Iterator<Server> iterator2 = categories.getServerListIn(category)
                    .getServers().iterator();
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
        for (String category : getCategoryNames()) {
            ServerList serverList = getServerListIn(category);
            if (serverList != null) {
                result.put(category,
                        serverList.diff(categories.getServerListIn(category)));
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