package com.swak.reactivex.threads;

import java.util.concurrent.LinkedBlockingQueue;
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
public final class WorkerContext extends ThreadPoolExecutor implements Context {

	private volatile PoolMetrics metrics;

	public WorkerContext(String prefix, int nThreads, boolean daemon, BlockedThreadChecker checker, long maxExecTime,
			TimeUnit maxExecTimeUnit) {
		super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
				new SwakThreadFactory(prefix, daemon, new AtomicInteger(0), checker, maxExecTime, maxExecTimeUnit));
	}

	/**
	 * 添加指标监控
	 */
	@Override
	public void execute(Runnable command) {
		Object metric = metrics != null ? metrics.submitted() : null;
		super.execute(() -> {
			Object usageMetric = null;
			if (metrics != null) {
				usageMetric = metrics.begin(metric);
			}
			boolean succeeded = executeTask(command);
			if (metrics != null) {
				metrics.end(usageMetric, succeeded);
			}
		});
	}
	
	

	@Override
	public void setPoolMetrics(PoolMetrics metrics) {
		this.metrics = metrics;
	}
}