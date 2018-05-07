package com.swak.reactivex.server.resources;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;

/**
 * EpollLoop
 * @author lifeng
 */
public class DefaultEpollLoopResources extends DefaultLoopResources {

	private static final long serialVersionUID = 1L;

	DefaultEpollLoopResources(String prefix, int selectCount, int workerCount, boolean daemon) {
		super(prefix, selectCount, workerCount, daemon);
	}

	@Override
	public Class<? extends ServerChannel> onServerChannel() {
		return EpollServerSocketChannel.class;
	}

	@Override
	public EventLoopGroup onServerSelect() {
		if (this.serverSelectLoops == null) {
			this.serverSelectLoops = new EpollEventLoopGroup(selectCount, threadFactory(this, "nio-select"));
		}
		return this.serverSelectLoops;
	}

	@Override
	public EventLoopGroup onServer() {
		if (this.serverLoops == null) {
			this.serverLoops = new EpollEventLoopGroup(workerCount, threadFactory(this, "nio-server"));
		}
		return this.serverLoops;
	}
}