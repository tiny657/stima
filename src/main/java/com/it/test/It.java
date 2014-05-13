package com.it.test;

import com.it.client.ItClient;
import com.it.common.Config;
import com.it.common.Sender;
import com.it.model.AllServer;
import com.it.model.Server;
import com.it.server.ItServer;

public class It {
    It(String[] args) throws Exception {
        Config.getInstance().init(args);

        // server
        ItServer itServer = new ItServer();
        itServer.start();

        // clients
        for (Server server : Config.getInstance().getServers()) {
            if (!server.equals(itServer.getHost(), itServer.getPort())) {
                ItClient itClient = new ItClient(server.getHost(), server.getPort());
                AllServer.getInstance().getServerInfo().add(server, itClient);
                itClient.start();
            }
        }
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