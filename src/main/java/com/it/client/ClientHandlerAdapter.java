/*
 * licensed to the apache software foundation (asf) under one or more contributor license
 * agreements. see the notice file distributed with this work for additional information regarding
 * copyright ownership. the asf licenses this file to you under the apache license, version 2.0 (the
 * "license"); you may not use this file except in compliance with the license. you may obtain a
 * copy of the license at
 *
 * http://www.apache.org/licenses/license-2.0
 *
 * unless required by applicable law or agreed to in writing, software distributed under the license
 * is distributed on an "as is" basis, without warranties or conditions of any kind, either express
 * or implied. see the license for the specific language governing permissions and limitations under
 * the license.
 */

package com.it.client;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.command.InfoCommand;
import com.it.config.MemberConfig;
import com.it.domain.AllMember;

@Sharable
public class ClientHandlerAdapter extends ChannelHandlerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(ClientHandlerAdapter.class);

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    if (MemberConfig.getInstance().isAutoSpread()) {
      InfoCommand infoCommand = new InfoCommand();
      infoCommand.setClusters(AllMember.getInstance().getClusters());
      ctx.channel().writeAndFlush(infoCommand);
    }
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {}

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
