package com.swak.reactivex.transport.resources;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;

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
	public Class<? extends Channel> onClientChannel() {
		return EpollSocketChannel.class;
	}

	@Override
	public EventLoopGroup onServerSelect() {
		if (this.serverSelectLoops == null) {
			this.serverSelectLoops = new EpollEventLoopGroup(selectCount, threadFactory(this, "epoll-acceptor-"));
			this.monitor("acceptor", this.serverSelectLoops);
		}
		return this.serverSelectLoops;
	}

	@Override
	public EventLoopGroup onServer() {
		if (this.serverLoops == null) {
			this.serverLoops = new EpollEventLoopGroup(workerCount, threadFactory(this, "epoll-eventloop-"));
			this.monitor("eventloop", this.serverLoops);
		}
		return this.serverLoops;
	}
}