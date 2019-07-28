package com.weibo.api.motan.transport.netty4;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.threads.ServerContext;
import com.swak.reactivex.transport.resources.LoopResources;
import com.weibo.api.motan.common.ChannelState;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.common.URLParamType;
import com.weibo.api.motan.exception.MotanFrameworkException;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import com.weibo.api.motan.rpc.URL;
import com.weibo.api.motan.transport.AbstractServer;
import com.weibo.api.motan.transport.MessageHandler;
import com.weibo.api.motan.transport.TransportException;
import com.weibo.api.motan.util.LoggerUtil;
import com.weibo.api.motan.util.StatisticCallback;
import com.weibo.api.motan.util.StatsUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;

/**
 * @author sunnights
 */
public class NettyServer extends AbstractServer implements StatisticCallback {
	protected NettyServerChannelManage channelManage = null;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private Channel serverChannel;
	private MessageHandler messageHandler;
	private ServerContext standardThreadExecutor = null;
	private LoopResources loopResources;
	private AtomicInteger rejectCounter = new AtomicInteger(0);

	public AtomicInteger getRejectCounter() {
		return rejectCounter;
	}

	public NettyServer(URL url, MessageHandler messageHandler) {
		super(url);
		this.messageHandler = messageHandler;
		this.loopResources = Contexts.createEventLoopResources(LoopResources.transportModeFitOs(), 1, -1, "Motan.",
				false, 2, TimeUnit.SECONDS);
	}

	@Override
	public boolean isBound() {
		return serverChannel != null && serverChannel.isActive();
	}

	@Override
	public Response request(Request request) throws TransportException {
		throw new MotanFrameworkException("NettyServer request(Request request) method not support: url: " + url);
	}

	@Override
	public boolean open() {
		if (isAvailable()) {
			LoggerUtil.warn("NettyServer ServerChannel already Open: url=" + url);
			return state.isAliveState();
		}
		if (bossGroup == null) {
			bossGroup = loopResources.onServerSelect();
			workerGroup = loopResources.onServer();
		}

		LoggerUtil.info("NettyServer ServerChannel start Open: url=" + url);
		boolean shareChannel = url.getBooleanParameter(URLParamType.shareChannel.getName(),
				URLParamType.shareChannel.getBooleanValue());
		final int maxContentLength = url.getIntParameter(URLParamType.maxContentLength.getName(),
				URLParamType.maxContentLength.getIntValue());
		int maxServerConnection = url.getIntParameter(URLParamType.maxServerConnection.getName(),
				URLParamType.maxServerConnection.getIntValue());
		int workerQueueSize = url.getIntParameter(URLParamType.workerQueueSize.getName(),
				URLParamType.workerQueueSize.getIntValue());

		int minWorkerThread, maxWorkerThread;

		if (shareChannel) {
			minWorkerThread = url.getIntParameter(URLParamType.minWorkerThread.getName(),
					MotanConstants.NETTY_SHARECHANNEL_MIN_WORKDER);
			maxWorkerThread = url.getIntParameter(URLParamType.maxWorkerThread.getName(),
					MotanConstants.NETTY_SHARECHANNEL_MAX_WORKDER);
		} else {
			minWorkerThread = url.getIntParameter(URLParamType.minWorkerThread.getName(),
					MotanConstants.NETTY_NOT_SHARECHANNEL_MIN_WORKDER);
			maxWorkerThread = url.getIntParameter(URLParamType.maxWorkerThread.getName(),
					MotanConstants.NETTY_NOT_SHARECHANNEL_MAX_WORKDER);
		}

		standardThreadExecutor = (standardThreadExecutor != null && !standardThreadExecutor.isShutdown())
				? standardThreadExecutor
				: Contexts.createServerContext("Motan.NettyServer-" + url.getServerPortStr(), minWorkerThread,
						maxWorkerThread, workerQueueSize, 60, TimeUnit.SECONDS, new AbortPolicy());
		standardThreadExecutor.prestartAllCoreThreads();

		channelManage = new NettyServerChannelManage(maxServerConnection);

		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(bossGroup, workerGroup).channel(loopResources.onServerChannel())
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast("channel_manage", channelManage);
						pipeline.addLast("decoder", new NettyDecoder(codec, NettyServer.this, maxContentLength));
						pipeline.addLast("encoder", new NettyEncoder());
						NettyChannelHandler handler = new NettyChannelHandler(NettyServer.this, messageHandler,
								standardThreadExecutor);
						pipeline.addLast("handler", handler);
					}
				});
		serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(url.getPort()));
		channelFuture.syncUninterruptibly();
		serverChannel = channelFuture.channel();
		state = ChannelState.ALIVE;
		StatsUtil.registryStatisticCallback(this);
		LoggerUtil.info("NettyServer ServerChannel finish Open: url=" + url);
		return state.isAliveState();
	}

	@Override
	public synchronized void close() {
		close(0);
	}

	@Override
	public synchronized void close(int timeout) {
		if (state.isCloseState()) {
			return;
		}

		try {
			cleanup();
			if (state.isUnInitState()) {
				LoggerUtil.info("NettyServer close fail: state={}, url={}", state.value, url.getUri());
				return;
			}

			// 设置close状态
			state = ChannelState.CLOSE;
			LoggerUtil.info("NettyServer close Success: url={}", url.getUri());
		} catch (Exception e) {
			LoggerUtil.error("NettyServer close Error: url=" + url.getUri(), e);
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
		// close all clients's channel
		if (channelManage != null) {
			channelManage.close();
		}
		// shutdown the threadPool
		if (standardThreadExecutor != null) {
			standardThreadExecutor.shutdownNow();
		}
		// 取消统计回调的注册
		StatsUtil.unRegistryStatisticCallback(this);
	}

	@Override
	public boolean isClosed() {
		return state.isCloseState();
	}

	@Override
	public boolean isAvailable() {
		return state.isAliveState();
	}

	@Override
	public URL getUrl() {
		return url;
	}

	@Override
	public String statisticCallback() {
		return String.format(
				"identity: %s connectionCount: %s taskCount: %s queueCount: %s maxThreadCount: %s maxTaskCount: %s executorRejectCount: %s",
				url.getIdentity(), channelManage.getChannels().size(), standardThreadExecutor.getSubmittedTasksCount(),
				standardThreadExecutor.getQueue().size(), standardThreadExecutor.getMaximumPoolSize(),
				standardThreadExecutor.getMaxSubmittedTaskCount(), rejectCounter.getAndSet(0));
	}
}
