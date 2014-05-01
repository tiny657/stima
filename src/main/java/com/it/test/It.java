package com.it.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.it.client.ItClient;
import com.it.common.Config;
import com.it.common.Sender;
import com.it.model.Server;
import com.it.server.ItServer;

public class It {
    private String profile;

    It(String[] args) throws Exception {
        Config config = new Config(args);
        profile = "me" + config.getPort();

        ExecutorService executor = Executors.newFixedThreadPool(config
                .getServers().size() + 1);

        // clients
        for (Server server : config.getServers()) {
            executor.execute(new ItClient(profile, server.getHost(), server
                    .getPort()));
        }

        // server
        executor.execute(new ItServer(config.getHost(), config.getPort()));
    }
    
    public String getProfile() {
        return profile;
    }

    public void sendBroadcast(String targetCategory, String message) {
        Sender.sendBroadcast(targetCategory, message);
    }

    public void sendAnycast(String targetCategory, String message) {
        Sender.sendAnycast(targetCategory, message);
    }

    public void sendUnicast(String targetHost, int targetPort, String message) {
        Sender.sendUnicast(targetHost, targetPort, message);
    }
}