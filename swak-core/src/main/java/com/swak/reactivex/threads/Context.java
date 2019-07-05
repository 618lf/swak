package com.swak.reactivex.threads;

import com.swak.meters.PoolMetrics;

/**
 * The execution context of a ThreadPool execution.
 * 
 * @author lifeng
 */
public interface Context {

	/**
	 * 线程执行的包装
	 * 
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
	@SuppressWarnings("rawtypes")
	void setPoolMetrics(PoolMetrics metrics);
}