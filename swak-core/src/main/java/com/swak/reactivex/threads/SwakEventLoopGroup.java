package com.swak.reactivex.threads;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.util.concurrent.AbstractEventExecutorGroup;
import io.netty.util.concurrent.DefaultEventExecutorChooserFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.Future;

/**
 * 仅仅是一个代理对象
 * 
 * @author lifeng
 */
public class SwakEventLoopGroup extends AbstractEventExecutorGroup implements EventLoopGroup {

	private final EventLoopContext[] children;
	private final Set<EventExecutor> readonlyChildren;
	private final EventExecutorChooserFactory.EventExecutorChooser chooser;
	private final EventLoopGroup eventLoopGroup;

	public SwakEventLoopGroup(EventLoopGroup eventLoopGroup, Consumer<EventLoopContext> apply) {
		this.eventLoopGroup = eventLoopGroup;
		int nThreads = ((MultithreadEventLoopGroup) eventLoopGroup).executorCount();
		children = new EventLoopContext[nThreads];
		for (int i = 0; i < nThreads; i++) {
			EventLoopContext context = new EventLoopContext(this.eventLoopGroup.next());
			apply.accept(context);
			children[i] = context;
		}
		chooser = DefaultEventExecutorChooserFactory.INSTANCE.newChooser(children);
		Set<EventExecutor> childrenSet = new LinkedHashSet<EventExecutor>(children.length);
		Collections.addAll(childrenSet, children);
		readonlyChildren = Collections.unmodifiableSet(childrenSet);
	}

	@Override
	public boolean isShuttingDown() {
		return eventLoopGroup.isShuttingDown();
	}

	@Override
	public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
		return eventLoopGroup.shutdownGracefully(quietPeriod, timeout, unit);
	}

	@Override
	public Future<?> terminationFuture() {
		return eventLoopGroup.terminationFuture();
	}

	@Override
	public EventLoop next() {
		return (EventLoop) chooser.next();
	}

	@Override
	public Iterator<EventExecutor> iterator() {
		return readonlyChildren.iterator();
	}

	@Override
	public boolean isShutdown() {
		return eventLoopGroup.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return eventLoopGroup.isTerminated();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return eventLoopGroup.awaitTermination(timeout, unit);
	}

	@Override
	@Deprecated
	public void shutdown() {
		eventLoopGroup.shutdown();
	}

	@Override
	public ChannelFuture register(Channel channel) {
		return next().register(channel);
	}

	@Override
	public ChannelFuture register(ChannelPromise promise) {
		return next().register(promise);
	}

	@Override
	@Deprecated
	public ChannelFuture register(Channel channel, ChannelPromise promise) {
		return next().register(channel, promise);
	}
}