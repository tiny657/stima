package com.it.main;

import com.it.client.Client;
import com.it.client.ClientHandlerAdapter;
import com.it.common.Config;
import com.it.model.AllMember;
import com.it.model.Member;
import com.it.server.Server;
import com.it.server.ServerHandlerAdapter;

public class ItRunner {
    public static void execute(ServerHandlerAdapter serverHandlerAdapter,
            ClientHandlerAdapter clientHandlerAdapter, String[] args) {
        try {
            Config.getInstance().init(args);

            // server
            Server server = new Server();
            server.setServerHandler(serverHandlerAdapter);
            server.start();

            // clients
            for (Member member : Config.getInstance().getMembers()) {
                if (!member.equals(server.getHost(), server.getPort())) {
                    Client client = new Client(member);
                    client.setClientHandler(clientHandlerAdapter);
                    AllMember.getInstance().getMemberInfos()
                            .put(member, client);
                    client.start();
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