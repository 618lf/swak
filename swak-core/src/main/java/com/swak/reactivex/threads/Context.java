package com.swak.reactivex.threads;

import com.swak.meters.MetricsFactory;

/**
 * The execution context of a ThreadPool execution.
 *
 * @author: lifeng
 * @date: 2020/3/29 12:17
 */
public interface Context {

	/**
	 * 执行任务
	 *
	 * @param command 任务
	 */
	void execute(Runnable command);

	/**
	 * 线程执行的包装
	 *
	 * @param command 任务
	 * @return 是否有异常
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
	 * @param metricsFactory 监控工具
	 */
	default void applyMetrics(MetricsFactory metricsFactory) {
	}

	/**
	 * 创建顺序执行器
	 * 
	 * @return
	 */
	default OrderContext newOrderContext() {
		return new OrderContext(this);
	}
}