package com.swak.paxos.transport.server;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import com.swak.paxos.config.ServerConfig;
import com.swak.paxos.transport.ChannelState;
import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.transport.resources.LoopResources;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.SocketChannel;

/**
 * 开启一个Tcp服务器
 * 
 * @author lifeng
 * @date 2020年12月26日 下午7:35:10
 */
public class TcpServer extends AbstractServer {

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private Channel serverChannel;
	private LoopResources loopResources;
	private final ServerConfig config;

	public TcpServer(ServerConfig config) {
		this.config = config;
		this.loopResources = Contexts.createEventLoopResources(LoopResources.transportModeFitOs(), 1, -1, "Motan.",
				false, 2, TimeUnit.SECONDS);
	}

	/**
	 * 开启服务
	 * 
	 * @return
	 */
	public boolean open() {
		if (isAvailable()) {
			logger.warn("NettyServer ServerChannel already Open: ip{}, port{}", config.getsListenIp(),
					config.getListenPort());
			return state.isAliveState();
		}

		if (bossGroup == null) {
			bossGroup = loopResources.onServerSelect();
			workerGroup = loopResources.onServer();
		}

		logger.info("NettyServer ServerChannel start Open: ip{}, port{}", config.getsListenIp(),
				config.getListenPort());

		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(bossGroup, workerGroup).channel(loopResources.onServerChannel())
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast("handler", new TcpMessageHandler(messageHandler));
					}
				});
		serverBootstrap.childOption(ChannelOption.SO_RCVBUF, config.getRecvBufferSizeTcp());
		serverBootstrap.childOption(ChannelOption.SO_SNDBUF, config.getSendBufferSizeTcp());
		serverBootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK,
				new WriteBufferWaterMark(config.getWriteBufferLowWaterMark(), config.getWriteBufferHighWaterMark()));
		serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		ChannelFuture channelFuture = serverBootstrap
				.bind(new InetSocketAddress(config.getsListenIp(), config.getListenPort()));
		channelFuture.syncUninterruptibly();
		serverChannel = channelFuture.channel();
		state = ChannelState.ALIVE;
		logger.info("NettyServer ServerChannel finish Open: ip{}, port{}", config.getsListenIp(),
				config.getListenPort());
		return state.isAliveState();
	}

	public synchronized void close() {
		if (state.isCloseState()) {
			return;
		}

		try {
			cleanup();
			if (state.isUnInitState()) {
				logger.info("NettyServer close fail: ip{}, port{}", config.getsListenIp(), config.getListenPort());
				return;
			}

			// 设置close状态
			state = ChannelState.CLOSE;
			logger.info("NettyServer close Success: ip{}, port{}", config.getsListenIp(), config.getListenPort());
		} catch (Exception e) {
			logger.info("NettyServer close Error: ip{}, port{}", config.getsListenIp(), config.getListenPort());
		}
	}

	public void cleanup() {
		// close listen socket
		if (serverChannel != null) {
			serverChannel.close();
		}
		if (bossGroup != null) {
			bossGroup.shutdownGracefully();
			bossGroup = null;
		}
		if (workerGroup != null) {
			workerGroup.shutdownGracefully();
			workerGroup = null;
		}
	}
}