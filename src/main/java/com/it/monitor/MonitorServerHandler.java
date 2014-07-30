/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.it.monitor;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import com.it.domain.History;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.common.Utils;
import com.it.domain.AllMember;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;

public class MonitorServerHandler extends ChannelHandlerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(MonitorServerHandler.class);

  private static final String INDEX = "/index.html";
  private static final String ROOT = "/";
  private static final String INTERVAL = "/interval";
  private static final String HISTORY = "/history";

  @Override
  public void channelActive(ChannelHandlerContext ctx) {}

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    if (msg instanceof HttpRequest) {
      HttpRequest request = (HttpRequest) msg;

      if (is100ContinueExpected(request)) {
        send100Continue(ctx);
      }

      writeResponse(ctx, request);
    }
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }

  private boolean writeResponse(ChannelHandlerContext ctx, HttpRequest request) {
    boolean keepAlive = isKeepAlive(request);

    String content = getContent(request.getUri());

    FullHttpResponse response =
        new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));

    response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");

    if (keepAlive) {
      // Add 'Content-Length' header only for a keep-alive connection.
      response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
      response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
    }

    ctx.write(response);
    return keepAlive;
  }

  private String getContent(String path) {
    String content = StringUtils.EMPTY;
    if (StringUtils.equals(path, INTERVAL)) {
      content = Utils.toJson(AllMember.getInstance().getClusters().getMemberListMap());
    } else if (StringUtils.equals(path, HISTORY)) {
      content = Utils.toJson(History.getInstance().getAlerts());
    } else if (StringUtils.equals(path, ROOT)) {
      try {
        content = IOUtils.toString(getClass().getResourceAsStream(INDEX), "UTF-8");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return content;
  }

  private void send100Continue(ChannelHandlerContext ctx) {
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
    ctx.write(response);
  }
}
