package com.it.main;

import io.netty.channel.ChannelFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.client.Client;
import com.it.client.ClientHandlerAdapter;
import com.it.command.StartCommand;
import com.it.common.Config;
import com.it.common.Sender;
import com.it.model.AllMember;
import com.it.model.Member;
import com.it.model.Status;
import com.it.server.Server;
import com.it.server.ServerHandlerAdapter;

public class ItRunner {
    private static final Logger logger = LoggerFactory
            .getLogger(ItRunner.class);

    public static ItRunner instance = new ItRunner();
    private ServerHandlerAdapter serverHandlerAdapter;
    private ClientHandlerAdapter clientHandlerAdapter;

    public static ItRunner getInstance() {
        return instance;
    }

    public ServerHandlerAdapter getServerHandlerAdapter() {
        return serverHandlerAdapter;
    }

    public ClientHandlerAdapter getClientHandlerAdapter() {
        return clientHandlerAdapter;
    }

    public void execute(ServerHandlerAdapter serverHandlerAdapter,
            ClientHandlerAdapter clientHandlerAdapter, String[] args) {
        this.serverHandlerAdapter = serverHandlerAdapter;
        this.clientHandlerAdapter = clientHandlerAdapter;

        try {
            Config.getInstance().init(args);

            // server
            Server server = new Server();
            server.setServerHandler(serverHandlerAdapter);
            server.start();
            server.await();

            // clients
            for (Member member : Config.getInstance().getMembers()) {
                if (!member.equals(server.getHost(), server.getPort())) {
                    Client client = new Client(member);
                    client.setClientHandler(clientHandlerAdapter);
                    AllMember.getInstance().getMemberInfos()
                            .put(member, client);
                    client.start();
                    client.await();
                }
            }

            // set status to RUNNING
            AllMember.getInstance().getMember(server.getMyInfo()).setStatus(Status.RUNNING);;
            logger.info(AllMember.getInstance().toString());

            Thread.sleep(3000);

            // send start command
            Sender.sendBroadcast(new StartCommand(Config.getInstance().getHost(), Config.getInstance().getPort()));
        } catch (Exception e) {
            shutdownNow();
        }
    }

    private void shutdownNow() {
        Runtime.getRuntime().exit(-1);
    }
}