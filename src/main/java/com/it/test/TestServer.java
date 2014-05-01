package com.it.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.it.client.ItClient;
import com.it.common.Config;
import com.it.model.Server;
import com.it.server.ItServer;

public class TestServer {
    public static void main(String[] args) throws Exception {
        Config config = new Config(args);

        // clients
        ExecutorService executor = Executors.newFixedThreadPool(config
                .getServers().size());
        for (Server server : config.getServers()) {
            executor.execute(new ItClient("me" + config.getPort(), server.getHost(), server.getPort()));
        }

        // server
        new ItServer(config.getHost(), config.getPort()).run();
    }
}