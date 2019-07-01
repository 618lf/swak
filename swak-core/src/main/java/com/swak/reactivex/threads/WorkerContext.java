package com.swak.reactivex.threads;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.meters.PoolMetrics;

/**
 * 普通的线程池
 * 
 * @author lifeng
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class WorkerContext extends ThreadPoolExecutorDecorator implements Context {

	private volatile PoolMetrics metrics;

	public WorkerContext(String prefix, int nThreads, boolean daemon, BlockedThreadChecker checker, long maxExecTime,
			TimeUnit maxExecTimeUnit) {
		super((ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads,
				new SwakThreadFactory(prefix, daemon, new AtomicInteger(0), checker, maxExecTime, maxExecTimeUnit)));
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

	@Override
	public void setPoolMetrics(PoolMetrics metrics) {
		this.metrics = metrics;
	}
}