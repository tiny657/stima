package com.it.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.client.Client;
import com.it.command.Command;
import com.it.command.InfoCommand;
import com.it.command.StartCommand;
import com.it.command.StopCommand;
import com.it.common.Config;
import com.it.main.ClientHandler;
import com.it.main.TestCommand;
import com.it.model.AllMember;
import com.it.model.Clusters;
import com.it.model.Member;
import com.it.model.MemberList;
import com.it.model.Status;

public class ServerHandlerAdapter extends ChannelHandlerAdapter {
    private static final Logger logger = LoggerFactory
            .getLogger(ServerHandlerAdapter.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Command) {
            if (msg instanceof StartCommand) {
                StartCommand cmd = (StartCommand) msg;
                Member member = AllMember.getInstance().getMember(
                        cmd.getSrcHost(), cmd.getSrcPort());
                if (member != null) {
                    member.setStatus(Status.RUNNING);
                    logger.info("StartCommand was received from {}.",
                            cmd.toString());
                } else {
                    logger.error(
                            "StartCommand was received from {}.  But that isn't existed.",
                            cmd.toString());
                }
                ReferenceCountUtil.release(msg);
            } else if (msg instanceof StopCommand) {
                StopCommand cmd = (StopCommand) msg;
                Member member = AllMember.getInstance().getMember(
                        cmd.getSrcHost(), cmd.getSrcPort());
                if (member != null) {
                    member.setStatus(Status.STANDBY);
                    logger.info("StopCommand was received from {}.",
                            cmd.toString());
                } else {
                    logger.error(
                            "StopCommand was received from {}.  But that isn't existed.",
                            cmd.toString());
                }
                ReferenceCountUtil.release(msg);
            } else if (msg instanceof InfoCommand) {
                InfoCommand cmd = (InfoCommand) msg;
                Clusters clusters = cmd.getClusters();
                Member receivedMember = clusters.findMe();

                // compare the received properties.
                if (AllMember.getInstance().getClusters()
                        .isEarlier(clusters.getBootupTime())) {
                    if (AllMember.getInstance().getClusters().equals(clusters)) {
                        logger.info("properties is same.");
                    } else {
                        logger.info("properties is different.");
                        removeMembers(clusters);
                        addMembers(clusters);
                    }

                    logger.info("InfoCommand was received. {}.",
                            clusters.toString());
                } else {
                    logger.info("ignore the received InfoCommand because this server was started up late.");
                }

                // update the status of the sender.
                AllMember
                        .getInstance()
                        .getMember(receivedMember.getHost(),
                                receivedMember.getPort())
                        .setStatus(receivedMember.getStatus());

                ReferenceCountUtil.release(msg);
            }
            logger.info(AllMember.getInstance().toString());
        } else if (msg instanceof TestCommand) {
            logger.info("TestCommand was received.");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void removeMembers(Clusters clusters) {
        Map<String, MemberList> removedmember = AllMember.getInstance()
                .getClusters().diff(clusters);
        logger.info("removed member : {}", removedmember.toString());

        for (String cluster : removedmember.keySet()) {
            // remove clister
            if (AllMember.getInstance().getMemberListIn(cluster).size() == removedmember
                    .get(cluster).size()) {
                logger.info("removed cluster: {}", cluster);
                AllMember.getInstance().removeCluster(cluster);
                Config.getInstance().removeCluster(cluster);
            }

            for (Member member : removedmember.get(cluster).getMembers()) {
                // stop client thread
                AllMember.getInstance().getMemberInfos().getClient(member)
                        .interrupt();

                // remove client and client info
                AllMember.getInstance().removeMember(cluster, member);
                AllMember.getInstance().getMemberInfos().removeInfo(member);
                Config.getInstance().removeMember(cluster, member);
            }
        }
    }

    private void addMembers(Clusters clusters) {
        Map<String, MemberList> addedMember = clusters.diff(AllMember
                .getInstance().getClusters());
        logger.info("added member : " + addedMember.toString());

        for (String cluster : addedMember.keySet()) {
            AllMember.getInstance().addCluster(cluster);
            for (Member member : addedMember.get(cluster).getMembers()) {
                // start client
                Client client = new Client(member);
                client.setClientHandler(new ClientHandler());
                client.start();

                // add client data
                AllMember.getInstance().addMember(cluster, member);
                AllMember.getInstance().getMemberInfos().put(member, client);
                Config.getInstance().addMember(cluster, member);
            }
        }
    }
}