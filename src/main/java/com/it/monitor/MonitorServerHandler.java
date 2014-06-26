package com.it.monitor;

import static io.netty.handler.codec.http.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MonitorServerHandler extends SimpleChannelInboundHandler<Object> {

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

      String url = request.getUri().toString();

      buf.setLength(0);
      buf.append("<html><head>");
      buf.append("<script src=//ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.min.js></script>");
      buf.append("<script src=//cdn.jsdelivr.net/jquery.flot/0.8.3/jquery.flot.js></script>");
      buf.append("<script>");
      buf.append("$(function() {");
      buf.append("$.plot($(\"#placeholder\"), [ [[0, 0], [1, 1]] ], { yaxis: { max: 1 } });");
      buf.append("});");
      buf.append("</script>");
      buf.append("</head>");
      buf.append("<body>");
      buf.append("<div style='width:800px; height:400px'>");
      buf.append("<div id=placeholder></div>");
      buf.append("</div>");
      buf.append("</body>");
      buf.append("</html>");

      // buf.append(CollectorListener.getInstance().getHistory().toString());

      // param
      QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
      Map<String, List<String>> params = queryStringDecoder.parameters();
      if (!params.isEmpty()) {
        for (Entry<String, List<String>> p : params.entrySet()) {
          String key = p.getKey();
          List<String> vals = p.getValue();
          for (String val : vals) {
            // buf.append("PARAM: ").append(key).append(" = ") .append(val).append("\r\n");
          }
        }
      }

      appendDecoderResult(buf, request);
    }

    if (msg instanceof HttpContent) {
      HttpContent httpContent = (HttpContent) msg;

      ByteBuf content = httpContent.content();
      if (content.isReadable()) {
        buf.append("CONTENT: ");
        buf.append(content.toString(CharsetUtil.UTF_8));
        buf.append("\r\n");
        appendDecoderResult(buf, request);
      }

      if (msg instanceof LastHttpContent) {
        LastHttpContent trailer = (LastHttpContent) msg;
        if (!trailer.trailingHeaders().isEmpty()) {
          buf.append("\r\n");
          for (String name : trailer.trailingHeaders().names()) {
            for (String value : trailer.trailingHeaders().getAll(name)) {
              buf.append("TRAILING HEADER: ");
              buf.append(name).append(" = ").append(value).append("\r\n");
            }
          }
          buf.append("\r\n");
        }

        writeResponse(trailer, ctx);
      }
    }
  }

  private static void appendDecoderResult(StringBuilder buf, HttpObject o) {
    DecoderResult result = o.getDecoderResult();
    if (result.isSuccess()) {
      return;
    }

    buf.append(".. WITH DECODER FAILURE: ");
    buf.append(result.cause());
    buf.append("\r\n");
  }

  private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
    // Decide whether to close the connection or not.
    boolean keepAlive = isKeepAlive(request);
    // Build the response object.
    FullHttpResponse response =
        new DefaultFullHttpResponse(HTTP_1_1, currentObj.getDecoderResult().isSuccess() ? OK
            : BAD_REQUEST, Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));

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
