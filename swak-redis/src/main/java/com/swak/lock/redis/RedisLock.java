package com.swak.lock.redis;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import org.springframework.beans.factory.DisposableBean;

import com.swak.meters.ExecutorMetrics;
import com.swak.reactivex.transport.resources.EventLoopFactory;

/**
 * 基于 redis 的一把锁 可以用于分布式的环境
 * 
 * @author lifeng
 *
 * @param <T>
 */
public class RedisLock implements ExecutorMetrics, DisposableBean {

	private static AtomicLong counter = new AtomicLong(0);

	private ExecutorService executor;
	private StrictRedisLock _lock;

	public RedisLock(String name) {
		executor = Executors.newFixedThreadPool(1, new EventLoopFactory(true, "SWAK.lock-thread", counter));
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
			try  {
				value = _lock.doHandler(handler);
			}catch (Exception e) {
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
		executor.shutdown();
	}
}