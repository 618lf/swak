package com.swak.paxos.transport.server;

import com.swak.paxos.transport.Message;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * Tcp 消息处理
 * 
 * @author DELL
 */
public class TcpMessageHandler extends ChannelDuplexHandler {

	private MessageHandler handler;

	public TcpMessageHandler(MessageHandler handler) {
		this.handler = handler;
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
		Message request = new Message();
		handler.handle(ctx.channel(), request);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.channel().close();
	}
}
