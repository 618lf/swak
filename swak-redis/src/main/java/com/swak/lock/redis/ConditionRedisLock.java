package com.swak.lock.redis;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Supplier;

/**
 * 基于条件的 redis lock 需要保证需要执行的任务是可以复制的 和其他因数没有关系
 * 
 * @author lifeng
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ConditionRedisLock extends OrderRedisLock {

	LinkedTransferQueue<CompletableFuture> queue;

	public ConditionRedisLock(String name) {
		super(name);
		queue = new LinkedTransferQueue<>();
	}

	/**
	 * 执行代码 后续代码必须切换线程执行
	 * 
	 * @param handler
	 * @return
	 */
	public <T> CompletableFuture<T> execute(Supplier<T> handler) {
		CompletableFuture<T> future = new CompletableFuture<>();
		queue.add(future);
		task(handler);
		return future;
	}

	// 单线程，先创建和请求数相当的任务
	private <T> void task(Supplier<T> handler) {
		executor.execute(() -> {
			this.doHandler(handler);
		});
	}

	// 单线程执行
	private <T> void doHandler(Supplier<T> handler) {

		// 判断本次任务是否需要执行
		CompletableFuture oneFuture = queue.poll();
		if (oneFuture == null) {
			return;
		}

		// 执行任务
		T value = null;
		try {
			value = _lock.doHandler(handler);
		} catch (Exception e) {
		}
		final Object _value = value;

		// 完成这一刻的所有任务
		oneFuture.complete(_value);
		int size = queue.size();
		for (int i = 0; i < size; i++) {
			CompletableFuture future = queue.poll();
			if (future != null) {
				future.complete(_value);
			}
		}
	}
}