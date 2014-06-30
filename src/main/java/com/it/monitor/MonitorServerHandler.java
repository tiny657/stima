package com.it.monitor;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class MonitorServerHandler extends SimpleChannelInboundHandler<Object> {
  private static final Logger logger = LoggerFactory.getLogger(MonitorServerHandler.class);

  private HttpRequest request;
  private final StringBuilder buf = new StringBuilder();

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  protected void messageReceived(ChannelHandlerContext ctx, Object msg) {
    if (msg instanceof HttpRequest) {
      HttpRequest request = this.request = (HttpRequest) msg;

      if (is100ContinueExpected(request)) {
        send100Continue(ctx);
      }

      writeResponse(ctx, request);
    }
  }

  private boolean writeResponse(ChannelHandlerContext ctx, HttpRequest request) {
    // Decide whether to close the connection or not.
    boolean keepAlive = isKeepAlive(request);

    String content = getContent(request.getUri());

    // Build the response object.
    FullHttpResponse response =
        new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));

    response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");

    if (keepAlive) {
      // Add 'Content-Length' header only for a keep-alive connection.
      response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
      // Add keep alive header as per:
      // -
      // http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
      response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
    }

    // Write the response.
    ctx.write(response);
    return keepAlive;
  }

  private String getContent(String path) {
    String content = StringUtils.EMPTY;
    if (StringUtils.equals(path, "/")) {
      try {
        content = IOUtils.toString(getClass().getResourceAsStream("/index.html"), "UTF-8");
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else if (StringUtils.equals(path, "/interval")) {
      content = "{test: 1}";
    }
    return content;
  }

  private static void send100Continue(ChannelHandlerContext ctx) {
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
    ctx.write(response);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
