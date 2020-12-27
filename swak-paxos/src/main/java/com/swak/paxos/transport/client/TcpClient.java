package com.swak.paxos.transport.client;

import java.util.concurrent.TimeUnit;

import com.swak.paxos.config.ClientConfig;
import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.transport.resources.LoopResources;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.SocketChannel;

/**
 * Tcp 客户端
 * 
 * @author DELL
 */
public class TcpClient extends AbstractClient {

	private final LoopResources loopResources = Contexts.createEventLoopResources(LoopResources.transportModeFitOs(), 1,
			-1, "Motan.", false, 2, TimeUnit.SECONDS);

	private final ClientConfig config;
	private Bootstrap bootstrap;

	public TcpClient(ClientConfig config) {
		this.config = config;
	}

	public void open() {
		bootstrap = new Bootstrap();
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeout());
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.SO_RCVBUF, config.getRecvBufferSizeTcp());
		bootstrap.option(ChannelOption.SO_SNDBUF, config.getSendBufferSizeTcp());
		bootstrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK,
				new WriteBufferWaterMark(config.getWriteBufferLowWaterMark(), config.getWriteBufferHighWaterMark()));

		bootstrap.group(loopResources.onClient()).channel(loopResources.onClientChannel())
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {

					}
				});
	}
}
