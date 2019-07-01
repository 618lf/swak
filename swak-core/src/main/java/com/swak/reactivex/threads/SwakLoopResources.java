package com.swak.reactivex.threads;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.swak.reactivex.transport.TransportMode;
import com.swak.reactivex.transport.resources.LoopResources;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;

/**
 * 代理 LoopResources
 * 
 * @author lifeng
 */
public class SwakLoopResources implements LoopResources {

	private LoopResources loopResources;
	private SwakEventLoopGroup eventLoopGroup;
	private Consumer<EventLoopContext> apply;

	public SwakLoopResources(TransportMode mode, String prefix, int selectCount, int workerCount, boolean daemon,
			BlockedThreadChecker checker, long maxExecTime, TimeUnit maxExecTimeUnit,
			Consumer<EventLoopContext> apply) {
		loopResources = LoopResources.create(mode, prefix, selectCount, workerCount, daemon, checker, maxExecTime,
				maxExecTimeUnit);
		this.apply = apply;
	}

	@Override
	public Class<? extends ServerChannel> onServerChannel() {
		return loopResources.onServerChannel();
	}

	@Override
	public EventLoopGroup onServerSelect() {
		return loopResources.onServerSelect();
	}

	@Override
	public EventLoopGroup onServer() {
		EventLoopGroup server = loopResources.onServer();
		eventLoopGroup = new SwakEventLoopGroup(server, apply);
		return eventLoopGroup;
	}

	@Override
	public Class<? extends Channel> onClientChannel() {
		return loopResources.onClientChannel();
	}

	@Override
	public EventLoopGroup onClient() {
		EventLoopGroup client = loopResources.onClient();
		eventLoopGroup = new SwakEventLoopGroup(client, apply);
		return eventLoopGroup;
	}
}