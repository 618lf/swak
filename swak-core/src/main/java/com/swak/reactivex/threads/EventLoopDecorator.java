package com.swak.reactivex.threads;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ProgressivePromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;

public class EventLoopDecorator implements EventLoop {

	private final EventLoop eventLoop;

	public EventLoopDecorator(EventLoop eventLoop) {
		this.eventLoop = eventLoop;
	}

	@Override
	public boolean inEventLoop() {
		return eventLoop.inEventLoop();
	}

	@Override
	public boolean inEventLoop(Thread thread) {
		return eventLoop.inEventLoop(thread);
	}

	@Override
	public <V> Promise<V> newPromise() {
		return eventLoop.newPromise();
	}

	@Override
	public <V> ProgressivePromise<V> newProgressivePromise() {
		return eventLoop.newProgressivePromise();
	}

	@Override
	public <V> Future<V> newSucceededFuture(V result) {
		return eventLoop.newSucceededFuture(result);
	}

	@Override
	public <V> Future<V> newFailedFuture(Throwable cause) {
		return eventLoop.newFailedFuture(cause);
	}

	@Override
	public boolean isShuttingDown() {
		return eventLoop.isShuttingDown();
	}

	@Override
	public Future<?> shutdownGracefully() {
		return eventLoop.shutdownGracefully();
	}

	@Override
	public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
		return eventLoop.shutdownGracefully(quietPeriod, timeout, unit);
	}

	@Override
	public Future<?> terminationFuture() {
		return eventLoop.terminationFuture();
	}

	@Override
	@Deprecated
	public void shutdown() {
		eventLoop.shutdown();
	}

	@Override
	@Deprecated
	public List<Runnable> shutdownNow() {
		return eventLoop.shutdownNow();
	}

	@Override
	public Iterator<EventExecutor> iterator() {
		return eventLoop.iterator();
	}

	@Override
	public Future<?> submit(Runnable task) {
		return eventLoop.submit(task);
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		return eventLoop.submit(task, result);
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		return eventLoop.submit(task);
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		return eventLoop.schedule(command, delay, unit);
	}

	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		return eventLoop.schedule(callable, delay, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		return eventLoop.scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		return eventLoop.scheduleWithFixedDelay(command, initialDelay, delay, unit);
	}

	@Override
	public boolean isShutdown() {
		return eventLoop.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return eventLoop.isTerminated();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return eventLoop.awaitTermination(timeout, unit);
	}

	@Override
	public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
			throws InterruptedException {
		return eventLoop.invokeAll(tasks);
	}

	@Override
	public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout,
			TimeUnit unit) throws InterruptedException {
		return eventLoop.invokeAll(tasks, timeout, unit);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		return eventLoop.invokeAny(tasks);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return eventLoop.invokeAny(tasks, timeout, unit);
	}

	@Override
	public void execute(Runnable command) {
		eventLoop.execute(command);
	}

	@Override
	public EventLoop next() {
		return eventLoop.next();
	}

	@Override
	public ChannelFuture register(Channel channel) {
		return eventLoop.register(channel);
	}

	@Override
	public ChannelFuture register(ChannelPromise promise) {
		return eventLoop.register(promise);
	}

	@Override
	@Deprecated
	public ChannelFuture register(Channel channel, ChannelPromise promise) {
		return eventLoop.register(channel, promise);
	}

	@Override
	public EventLoopGroup parent() {
		return eventLoop.parent();
	}
}
