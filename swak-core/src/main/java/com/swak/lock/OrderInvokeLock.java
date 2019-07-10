package com.swak.lock;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.beans.factory.DisposableBean;

import com.swak.reactivex.threads.Contexts;

/**
 * 按顺序执行代码的锁，目的不阻塞线程 请注册到spring中
 * 
 * @author lifeng
 */
public class OrderInvokeLock implements AsyncLock, DisposableBean {

	private int awaitTime = 30; // 30s
	protected final ExecutorService executor;
	protected final Lock _lock;

	/**
	 * @param lock
	 *            -- 真实的锁，局部锁，全局锁
	 * @param threads
	 *            -- 线程数
	 * @param maxExecSeconds
	 *            -- 最大的执行时间
	 */
	public OrderInvokeLock(Lock lock, int maxExecSeconds) {
		this.executor = Contexts.createWorkerContext("SWAK.lock-" + lock.name() + "-", 1, true, maxExecSeconds,
				TimeUnit.SECONDS);
		this._lock = lock;
	}

	/**
	 * @param lock
	 *            -- 真实的锁，局部锁，全局锁
	 * @param threads
	 *            -- 线程数
	 * @param maxExecSeconds
	 *            -- 最大的执行时间
	 */
	public OrderInvokeLock(Lock lock, int maxExecSeconds, int maxQueue, RejectedExecutionHandler handler) {
		this.executor = Contexts.createWorkerContext("SWAK.lock-" + lock.name() + "-", 1, true, maxExecSeconds,
				TimeUnit.SECONDS, maxQueue, handler);
		this._lock = lock;
	}

	/**
	 * 执行代码 后续代码必须切换线程执行
	 * 
	 * @param handler
	 * @return
	 */
	@Override
	public <T> CompletableFuture<T> execute(Supplier<T> handler) {
		CompletableFuture<T> future = new CompletableFuture<>();
		executor.execute(() -> {
			T value = null;
			try {
				value = this.doHandler(handler);
			} catch (Exception e) {
				future.completeExceptionally(e);
				return;
			}
			future.complete(value);
		});
		return future;
	}

	/**
	 * 销毁
	 */
	@Override
	public void destroy() throws Exception {
		try {
			executor.shutdown();
			if (!executor.awaitTermination(awaitTime, TimeUnit.SECONDS)) {
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			executor.shutdownNow();
		}
		try {
			this.unlock();
		} catch (Exception e) {
		}
	}

	// _lock
	@Override
	public String name() {
		return _lock.name();
	}

	@Override
	public <T> T doHandler(Supplier<T> handler) {
		return _lock.doHandler(handler);
	}

	@Override
	public boolean unlock() {
		return _lock.unlock();
	}
	
	/**
	 * 创建一个异步锁 -- 顺序执行代码
	 * 
	 * @param lock
	 * @param maxExecSeconds
	 * @return
	 */
	public static AsyncLock of(Lock lock, int maxExecSeconds) {
		return new OrderInvokeLock(lock, maxExecSeconds);
	}
}