package com.swak.rpc.server;

import com.swak.reactivex.transport.channel.ContextHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * RPC 的处理器
 * @author lifeng
 */
public class RpcServerHandler extends ChannelInboundHandlerAdapter{

	private final ContextHandler context;

	public RpcServerHandler(ContextHandler contextHandler) {
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
	 * 通道闲置
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}

	/**
	 * 获取数据
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		context.doChannel(ctx.channel(), msg);
	}

	/**
	 * 捕获异常 -- 都异常的不用处理，直接关闭就好了
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
		ctx.channel().close();
	}
}
