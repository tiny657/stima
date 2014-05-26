package com.it.server;

import com.it.client.Client;
import com.it.common.Config;
import com.it.model.AllMember;
import com.it.model.Member;

public class ItRunner {
    public static void main(String[] args) {
        try {
            Config.getInstance().init(args);

            // server
            Server server = new Server();
            server.start();

            // clients
            for (Member member : Config.getInstance().getMembers()) {
                if (!member.equals(server.getHost(), server.getPort())) {
                    Client client = new Client(member.getHost(),
                            member.getPort());
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