package com.swak.reactivex.threads;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.meters.PoolMetrics;

/**
 * 监控定时任务
 * 
 * @author lifeng
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ScheduledContext extends ScheduledThreadPoolExecutor implements Context {

	private volatile PoolMetrics metrics;

	public ScheduledContext(String prefix, int nThreads, boolean daemon, BlockedThreadChecker checker, long maxExecTime,
			TimeUnit maxExecTimeUnit) {
		super(nThreads,
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