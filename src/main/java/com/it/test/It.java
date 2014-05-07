package com.it.test;

import com.it.client.ItClient;
import com.it.common.Config;
import com.it.common.Sender;
import com.it.model.Server;
import com.it.server.ItServer;

public class It {
    It(String[] args) throws Exception {
        Config.getInstance().init(args);

        // clients
        for (Server server : Config.getInstance().getServers()) {
            new Thread(new ItClient(server.getHost(), server.getPort()))
                    .start();
        }

        // server
        new Thread(new ItServer()).start();
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