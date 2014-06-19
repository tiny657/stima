package com.it.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.client.Client;
import com.it.client.ClientHandlerAdapter;
import com.it.command.StartCommand;
import com.it.command.StopCommand;
import com.it.common.Sender;
import com.it.config.JoptConfig;
import com.it.config.MailConfig;
import com.it.config.MemberConfig;
import com.it.job.JobManager;
import com.it.model.AllMember;
import com.it.model.Clusters;
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
            initialize(args);
            Clusters clusters = AllMember.getInstance().getClusters();
            Member me = AllMember.getInstance().me();

            // server
            Server server = new Server(me);
            server.setServerHandler(serverHandlerAdapter);
            server.start();
            server.await();

            // clients
            for (String clusterName : clusters.getClusterNames()) {
                for (Member member : clusters.getMemberListIn(clusterName)
                        .getMembers()) {
                    if (!member.isMe()) {
                        Client client = new Client(member);
                        client.setClientHandler(clientHandlerAdapter);
                        client.start();
                        client.await();
                    }
                }
            }

            // change status to Running
            Member myInfo = server.getMyInfo();
            AllMember.getInstance()
                    .getMember(myInfo.getHost(), myInfo.getPort())
                    .setStatus(Status.RUNNING);

            logger.info(AllMember.getInstance().toString());

            Thread.sleep(MemberConfig.getInstance().getSpreadTime() * 1000);

            // broadcast StartCommand
            Sender.sendBroadcast(new StartCommand(MemberConfig.getInstance()
                    .getHost(), MemberConfig.getInstance().getPort()));
        } catch (Exception e) {
            e.printStackTrace();
            shutdown();
        }
    }

    public void shutdown() {
        Sender.sendBroadcast(new StopCommand(MemberConfig.getInstance()
                .getHost(), MemberConfig.getInstance().getPort()));

        try {
            // wait for 1000ms after sending StopCommand.
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Member me = AllMember.getInstance().me();
        // close server.
        AllMember.getInstance().getMemberInfos().getChannelFuture(me).channel()
                .close();

        // close client.
        Clusters clusters = AllMember.getInstance().getClusters();
        for (String clusterName : clusters.getClusterNames()) {
            for (Member member : clusters.getMemberListIn(clusterName)
                    .getMembers()) {
                if (!member.isMe()) {
                    AllMember.getInstance().getMemberInfos().getClient(member)
                            .shutdown();
                }
            }
        }
    }

    private void initialize(String[] args) {
        // config
        try {
            JoptConfig.getInstance().init(args);
            MemberConfig.getInstance().init(args);
            MailConfig.getInstance().init(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // monitor
        JobManager.getInstance().runCollectorJob();

    }
}