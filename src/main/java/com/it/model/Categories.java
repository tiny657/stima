package com.it.model;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class Categories {
    private Map<String, ServerList> serverListMap = Maps.newHashMap();

    public void addCategory(String[] categoryNames) {
        for (String categoryName : categoryNames) {
            addCategory(categoryName);
        }
    }

    public void addCategory(String categoryName) {
        serverListMap.put(categoryName, new ServerList());
    }

    public void addServer(String categoryName, Server server) {
        serverListMap.get(categoryName).addServer(server);
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

    public Map<String, ServerList> addedServerFrom(Categories categories) {
        System.out.println(AllServer.getInstance().getCategories().getServerListMap().toString());
        System.out.println(categories.getServerListMap().toString());
        Map<String, ServerList> result = Maps.newHashMap();
        for (String category : categories.getServerListMap().keySet()) {
            if (serverListMap.containsKey(category)) {
                SetView<Server> differenceServers = Sets.difference(categories
                        .getServerListMap().get(category).getServers(),
                        serverListMap.get(category).getServers());
                result.put(category, new ServerList(differenceServers));
            } else {
                result.put(category, categories.getServerListMap()
                        .get(category));
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