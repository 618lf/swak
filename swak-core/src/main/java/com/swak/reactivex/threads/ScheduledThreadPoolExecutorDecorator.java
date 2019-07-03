package com.swak.reactivex.threads;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 代理 ScheduledThreadPool
 * 
 * @author lifeng
 */
public class ScheduledThreadPoolExecutorDecorator implements ScheduledExecutorService {

	ScheduledThreadPoolExecutor scheduledThreadPool;

	public ScheduledThreadPoolExecutorDecorator(ScheduledThreadPoolExecutor scheduledThreadPool) {
		this.scheduledThreadPool = scheduledThreadPool;
	}

	@Override
	public void shutdown() {
		this.scheduledThreadPool.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return this.scheduledThreadPool.shutdownNow();
	}

	@Override
	public boolean isShutdown() {
		return this.scheduledThreadPool.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return this.scheduledThreadPool.isTerminated();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return this.scheduledThreadPool.awaitTermination(timeout, unit);
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		return this.scheduledThreadPool.submit(task);
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		return this.scheduledThreadPool.submit(task, result);
	}

	@Override
	public Future<?> submit(Runnable task) {
		return this.scheduledThreadPool.submit(task);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		return this.scheduledThreadPool.invokeAll(tasks);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		return this.scheduledThreadPool.invokeAll(tasks, timeout, unit);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		return this.scheduledThreadPool.invokeAny(tasks);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return this.scheduledThreadPool.invokeAny(tasks, timeout, unit);
	}

	@Override
	public void execute(Runnable command) {
		this.scheduledThreadPool.execute(command);
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		return this.scheduledThreadPool.schedule(command, delay, unit);
	}

	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		return this.scheduledThreadPool.schedule(callable, delay, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		return this.scheduledThreadPool.scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		return this.scheduledThreadPool.scheduleWithFixedDelay(command, initialDelay, delay, unit);
	}
}
