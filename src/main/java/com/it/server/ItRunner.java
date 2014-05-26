package com.it.server;

import com.it.client.ItClient;
import com.it.common.Config;
import com.it.model.AllMember;
import com.it.model.Member;

public class ItRunner {
    public static void main(String[] args) {
        try {
            Config.getInstance().init(args);

            // server
            ItServer itServer = new ItServer();
            itServer.start();

            // clients
            for (Member member : Config.getInstance().getMembers()) {
                if (!member.equals(itServer.getHost(), itServer.getPort())) {
                    ItClient itClient = new ItClient(member.getHost(),
                            member.getPort());
                    AllMember.getInstance().getMemberInfos()
                            .put(member, itClient);
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