package com.swak.http.server;

import com.swak.http.Executeable;
import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.http.metric.MetricCenter;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * 服务器处理
 * 
 * @author lifeng
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

	private final HttpServerContext context;
	private final Executeable executor;

	public HttpServerHandler(HttpServerContext context) {
		this.context = context;
		this.executor = context.getPool();
	}

	/**
	 * 通道激活
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		MetricCenter.channelActive();
	}

	/**
	 * 通道闲置
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		MetricCenter.channelInactive();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof FullHttpRequest) {
			MetricCenter.requestHandler();
			FullHttpRequest req = (FullHttpRequest) msg;
			String lookupPath = getLookupPath(req);
			executor.onExecute(lookupPath, ()-> {
				HttpServletRequest request = HttpServletRequest.build(ctx, req);
				HttpServletResponse response = request.getResponse();
				context.buildFilterChain().doFilter(request, response);
			});
		}
	}

	private String getLookupPath(FullHttpRequest request) {
		String url = request.uri();
		int pathEndPos = url.indexOf('?');
		return pathEndPos < 0 ? url : url.substring(0, pathEndPos);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
		Channel ch = ctx.channel();

		Throwable cause = t.getCause();
		if (cause instanceof TooLongFrameException) {
			sendError(ctx, HttpResponseStatus.BAD_REQUEST, null);
			return;
		}
		if (ch.isOpen()) {
			sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, null);
		}
		if (ctx.channel().isActive()) {
			sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, null);
		}
	}

	private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status, HttpRequest request) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
				Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
}
