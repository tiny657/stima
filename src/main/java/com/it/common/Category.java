package com.it.common;

import java.util.List;
import com.google.common.collect.Lists;

public class Category {
    private List<Server> servers = Lists.newArrayList();
    
    public void addServer(Server server) {
        servers.add(server);
    }
}