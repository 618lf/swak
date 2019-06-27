package com.swak.reactivex.threads;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.swak.meters.PoolMetrics;

/**
 * 普通的线程池
 * 
 * @author lifeng
 */
public final class WorkerContext extends ThreadPoolExecutorDecorator implements Context {

	private final PoolMetrics metrics;

	public WorkerContext(String prefix, int nThreads, BlockedThreadChecker checker, long maxExecTime,
			TimeUnit maxExecTimeUnit, PoolMetrics metrics) {
		super((ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads,
				new SwakThreadFactory(prefix, checker, maxExecTime, maxExecTimeUnit)));
		this.metrics = metrics;
	}

	public WorkerContext(String prefix, int nThreads, SwakThreadFactory threadFactory, PoolMetrics metrics) {
		super((ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads, threadFactory));
		this.metrics = metrics;
	}

	/**
	 * 添加指标监控
	 */
	@Override
	public void execute(Runnable command) {
		Object metric = metrics != null ? metrics.submitted() : null;
		super.execute(() -> {
			if (metrics != null) {
				metrics.begin(metric);
			}
			boolean succeeded = executeTask(command);
			if (metrics != null) {
				metrics.end(metric, succeeded);
			}
		});
	}
}