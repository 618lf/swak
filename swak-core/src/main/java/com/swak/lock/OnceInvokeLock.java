package com.swak.lock;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.function.Supplier;

/**
 * 一段时间之后只需要执行一次代码的锁。 例如10格請求執行鎖，但只要一個請求執行成功，其他請求可以使用這個結果。
 * 
 * @author lifeng
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class OnceInvokeLock extends OrderInvokeLock {

	private LinkedTransferQueue<CompletableFuture> queue;

	/**
	 * 线程池中只需有一个任务即可，其他的可以忽略
	 * 
	 * @param lock
	 * @param maxExecSeconds
	 */
	public OnceInvokeLock(Lock lock, int maxExecSeconds) {
		super(lock, maxExecSeconds, 1, new DiscardPolicy());
		queue = new LinkedTransferQueue<>();
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
		queue.add(future);
		executor.execute(() -> {
			this.queue(handler);
		});
		return future;
	}

	/**
	 * 处理队列中的任务
	 * 
	 * @param handler
	 */
	private <T> void queue(Supplier<T> handler) {
		for (;;) {

			/**
			 * 需要执行的任务
			 */
			CompletableFuture oneFuture = queue.poll();
			if (oneFuture == null) {
				return;
			}

			/**
			 * 执行成功为止
			 */
			T value = null;
			try {
				value = _lock.doHandler(handler);
			} catch (Exception e) {
				oneFuture.completeExceptionally(e);
				continue;
			}

			final Object _value = value;

			/**
			 * 完成这一刻的所有任务
			 */
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
}
