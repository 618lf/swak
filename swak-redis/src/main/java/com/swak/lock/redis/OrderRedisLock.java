package com.swak.lock.redis;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import org.springframework.beans.factory.DisposableBean;

import com.swak.reactivex.transport.resources.EventLoopFactory;
import com.swak.reactivex.transport.resources.EventLoops;

/**
 * 基于 redis 的一把锁 可以用于分布式的环境 按顺序执行代码，每次執行都會獲取鎖，之後在釋放鎖
 * 
 * @author lifeng
 */
public class OrderRedisLock implements DisposableBean {

	private static AtomicLong counter = new AtomicLong(0);

	private int awaitTime = 30; // 30s
	protected ExecutorService executor;
	protected StrictRedisLock _lock;

	public OrderRedisLock(String name) {
		executor = Executors.newFixedThreadPool(1, new EventLoopFactory(true, "SWAK.lock-" + name + "-", counter));
		EventLoops.register("lcok-" + name, executor);
		_lock = new StrictRedisLock(name);
	}

	/**
	 * 执行代码 后续代码必须切换线程执行
	 * 
	 * @param handler
	 * @return
	 */
	public <T> CompletableFuture<T> execute(Supplier<T> handler) {
		CompletableFuture<T> future = new CompletableFuture<>();
		executor.execute(() -> {
			T value = null;
			try {
				value = _lock.doHandler(handler);
			} catch (Exception e) {
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
			_lock.unlock();
		} catch (Exception e) {
		}
	}
}