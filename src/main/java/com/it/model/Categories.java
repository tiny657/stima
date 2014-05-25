package com.it.model;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;

public class Categories {
    private Date bootupTime = new Date();
    private Map<String, ServerList> serverListMap = Maps.newHashMap();
    private static final ServerList EMPTY_SERVERLIST = new ServerList();

    public Date getBootupTime() {
        return bootupTime;
    }

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
        if (serverList == EMPTY_SERVERLIST) {
            add(categoryName);
            serverList = getServerListIn(categoryName);
        }
        serverList.addServer(server);
    }

    public void remove(String categoryName, Server server) {
        ServerList serverList = getServerListIn(categoryName);
        if (serverList.hasServers()) {
            serverList.removeServer(server);
        }
    }

    public Set<String> getCategoryNames() {
        return serverListMap.keySet();
    }

    public ServerList getServerListIn(String category) {
        ServerList serverList = serverListMap.get(category);
        if (serverList == null) {
            return EMPTY_SERVERLIST;
        }
        return serverList;
    }

    public Server nextRunningServer(String category) {
        ServerList serverList = getServerListIn(category);
        return serverList.nextRunningServer();
    }

    public boolean setStatus(Server server, boolean isRunning) {
        for (Entry<String, ServerList> entry : serverListMap.entrySet()) {
            ServerList serverList = entry.getValue();
            if (serverList.setStatus(server.getHost(), server.getPort(),
                    isRunning)) {
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
            if (serverList != EMPTY_SERVERLIST) {
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