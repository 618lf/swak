package com.swak.http.server;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.http.pool.ConfigableThreadPool;

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
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * 服务器处理
 * 
 * @author lifeng
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

	private final HttpServerContext context;
	private final ConfigableThreadPool pool;

	public HttpServerHandler(HttpServerContext context) {
		this.context = context;
		this.pool = context.getPool();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof FullHttpRequest) {
			String lookupPath = getLookupPath((FullHttpRequest)msg);
			pool.onExecute(lookupPath, new HttpWorkTask(ctx, (FullHttpRequest) msg));
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

	/**
	 * 请求处理器
	 * 
	 * @author lifeng
	 */
	class HttpWorkTask implements Runnable {

		private ChannelHandlerContext ctx;
		private FullHttpRequest req;

		public HttpWorkTask(ChannelHandlerContext ctx, FullHttpRequest req) {
			this.ctx = ctx;
			this.req = req;
		}

		@Override
		public void run() {

			// 标准的 请求 < -- > 响应
			HttpServletResponse response = HttpServletResponse.build(HttpServerChannelInitializer.date);
			HttpServletRequest request = HttpServletRequest.build(ctx, req, response);

			// http 请求处理
			try {

				// 执行链
				context.buildFilterChain().doFilter(request, response);

				// 构建响应
				HttpResponse _response = response.render();
				boolean keepAlive = request.isKeepAlive();
				if (!keepAlive) {
					ctx.writeAndFlush(_response);
					ctx.close();
				} else {
					_response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
					ctx.writeAndFlush(_response);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

			// 释放资源 for gc
			finally {
				ReferenceCountUtil.release(req);
				IOUtils.closeQuietly(request);
				IOUtils.closeQuietly(response);
				request = null;
				response = null;
			}
		}
	}
}
