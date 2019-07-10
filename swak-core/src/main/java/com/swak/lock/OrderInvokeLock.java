package com.swak.lock;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
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
	protected ExecutorService executor;
	protected Lock _lock;

	public OrderInvokeLock(Lock lock) {
		executor = Contexts.createWorkerContext("SWAK.lock-" + lock.name() + "-", 1, true, 60, TimeUnit.SECONDS);
		_lock = lock;
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
}