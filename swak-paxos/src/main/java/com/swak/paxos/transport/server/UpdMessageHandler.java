package com.swak.paxos.transport.server;

import com.swak.paxos.transport.Message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * Upd 消息处理器
 * 
 * @author DELL
 */
public class UpdMessageHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	private MessageHandler handler;

	public UpdMessageHandler(MessageHandler handler) {
		this.handler = handler;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		handler.handle(ctx.channel(), null);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.channel().close();
	}
}