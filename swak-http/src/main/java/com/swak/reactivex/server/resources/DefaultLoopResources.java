package com.swak.reactivex.server.resources;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * 创建默认的 EventLoopGroup
 * 
 * @author lifeng
 */
public class DefaultLoopResources extends AtomicLong implements LoopResources {

	private static final long serialVersionUID = 1L;

	final String prefix;
	final boolean daemon;
	final int selectCount;
	final int workerCount;
	final EventLoopGroup serverLoops;
	final EventLoopGroup clientLoops;
	final EventLoopGroup serverSelectLoops;

	DefaultLoopResources(String prefix, int selectCount, int workerCount, boolean daemon) {
		this.daemon = daemon;
		this.workerCount = workerCount;
		this.prefix = prefix;
		this.serverLoops = new NioEventLoopGroup(workerCount, threadFactory(this, "nio-server"));
		this.clientLoops = new NioEventLoopGroup(workerCount, threadFactory(this, "nio-worker"));
		this.selectCount = workerCount;
		this.serverSelectLoops = this.serverLoops;
	}
	
	@Override
	public EventLoopGroup onServerSelect() {
		return serverSelectLoops;
	}

	@Override
	public EventLoopGroup onServer() {
		return serverLoops;
	}
	
	@Override
	public EventLoopGroup onClient() {
		return clientLoops;
	}

	static ThreadFactory threadFactory(DefaultLoopResources parent, String prefix) {
		return new EventLoopFactory(parent.daemon, parent.prefix + "-" + prefix, parent);
	}

	final static class EventLoopFactory implements ThreadFactory {

		final boolean daemon;
		final AtomicLong counter;
		final String prefix;

		EventLoopFactory(boolean daemon, String prefix, AtomicLong counter) {
			this.daemon = daemon;
			this.counter = counter;
			this.prefix = prefix;
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setDaemon(daemon);
			t.setName(prefix + "-" + counter.incrementAndGet());
			return t;
		}
	}
}
