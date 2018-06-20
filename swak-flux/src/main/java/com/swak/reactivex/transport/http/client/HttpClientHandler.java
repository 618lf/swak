package com.swak.reactivex.transport.http.client;

import com.swak.reactivex.transport.channel.ContextHandler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 激活就发送数据
 * @author lifeng
 */
public class HttpClientHandler extends ChannelDuplexHandler {
	
	ContextHandler contextHandler;
	
	public HttpClientHandler(ContextHandler contextHandler) {
		this.contextHandler = contextHandler;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		contextHandler.doChannel(ctx.channel(), null);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}
}