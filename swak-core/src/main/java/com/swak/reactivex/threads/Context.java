package com.swak.reactivex.threads;

import com.swak.meters.MetricsFactory;

/**
 * The execution context of a ThreadPool execution.
 * 
 * @author lifeng
 */
public interface Context {

	/**
	 * 执行任务
	 * 
	 * @param command
	 */
	void execute(Runnable command);

	/**
	 * 线程执行的包装
	 * @see 刚开始以为command.run() 被try catch 之后如果抛出异常会导致无法继续运行下去。
	 *      CompletableFuture 可以避免，因为有进一步的封装
	 * @param command
	 * @return
	 */
	default boolean executeTask(Runnable command) {
		Thread th = Thread.currentThread();
		if (!(th instanceof SwakThread)) {
			throw new IllegalStateException("Uh oh! context executing with wrong thread! " + th);
		}
		SwakThread current = (SwakThread) th;
		current.executeStart();
		try {
			current.setContext(this);
			command.run();
			return true;
		} catch (Throwable t) {
			return false;
		} finally {
			current.executeEnd();
		}
	}

	/**
	 * 设置监控工具
	 * 
	 * @param metrics
	 */
	default void applyMetrics(MetricsFactory metricsFactory) {
	}
}