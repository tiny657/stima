package com.it.server;

import com.it.client.ItClient;
import com.it.common.Config;
import com.it.model.AllServer;
import com.it.model.Server;

public class ItRunner {
    public static void main(String[] args) {
        try {
            Config.getInstance().init(args);

            // server
            ItServer itServer = new ItServer();
            itServer.start();

            // clients
            for (Server server : Config.getInstance().getServers()) {
                if (!server.equals(itServer.getHost(), itServer.getPort())) {
                    ItClient itClient = new ItClient(server.getHost(),
                            server.getPort());
                    AllServer.getInstance().getServerInfos()
                            .put(server, itClient);
                    itClient.start();
                }
            }
        } catch (Exception e) {
            shutdownNow();
        }
    }

    private static void shutdownNow() {
        Runtime.getRuntime().exit(-1);
    }
}