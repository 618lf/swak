package com.swak.reactivex.transport.http.server;

import com.swak.reactivex.transport.channel.ChannelOperations;
import com.swak.reactivex.transport.channel.ContextHandler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

/**
 * 服务器处理
 * 
 * @author lifeng
 */
public class HttpServerHandler extends ChannelDuplexHandler {

	private final ContextHandler context;

	public HttpServerHandler(ContextHandler contextHandler) {
		this.context = contextHandler;
	}
	
	/**
	 * 通道激活
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	/**
	 * 通道闲置, 关闭通道, 释放资源
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		ChannelOperations<?,?> ops = ChannelOperations.get(ctx.channel());
//		if (ops != null) {
//			ops.onChannelClose();
//		}
		super.channelInactive(ctx);
		context.terminateChannel(ctx.channel());
	}

	/**
	 * 读取数据，绑定资源
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			ChannelOperations<?,?> ops = context.doChannel(ctx.channel(), msg);
			if (ops != null) {
				ops.onHandlerStart();
			} else {
				ReferenceCountUtil.release(msg);
			}
		}catch (Exception e) {
			exceptionCaught(ctx, e);
		}
	}

	/**
	 * 写数据，关闭后释放资源
	 */
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		promise.addListener(ChannelFutureListener.CLOSE);
		super.write(ctx, msg, promise);
	}

	/**
	 * 捕获异常 -- 都异常的不用处理，直接关闭就好了, 释放资源
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable err) {
		context.terminateChannel(ctx.channel());
	}
}