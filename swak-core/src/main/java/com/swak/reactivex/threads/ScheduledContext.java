package com.swak.reactivex.threads;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.meters.MetricsFactory;
import com.swak.meters.PoolMetrics;

/**
 * 监控定时任务
 * 
 * @author lifeng
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class ScheduledContext extends ScheduledThreadPoolExecutor implements Context {

	private volatile PoolMetrics metrics;
	private String name;
	private int nThreads;

	public ScheduledContext(String prefix, int nThreads, boolean daemon, BlockedThreadChecker checker, long maxExecTime,
			TimeUnit maxExecTimeUnit) {
		super(nThreads,
				new SwakThreadFactory(prefix, daemon, new AtomicInteger(0), checker, maxExecTime, maxExecTimeUnit));
		this.name = prefix;
		this.nThreads = nThreads;
	}

	/**
	 * 只执行一次
	 */
	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		return super.schedule(decorateTask(command), delay, unit);
	}

	/**
	 * 执行多次
	 */
	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		return super.scheduleAtFixedRate(decorateTask(command), initialDelay, period, unit);
	}

	/**
	 * 执行多次
	 */
	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		return super.scheduleWithFixedDelay(decorateTask(command), initialDelay, delay, unit);
	}

	/**
	 * 装饰需要监控的代码
	 * 
	 * @param command
	 * @return
	 */
	private Runnable decorateTask(Runnable command) {
		return () -> {
			Object usageMetric = null;
			if (metrics != null) {
				usageMetric = metrics.begin(null);
			}
			boolean succeeded = executeTask(command);
			if (metrics != null) {
				metrics.end(usageMetric, succeeded);
			}
		};
	}

	@Override
	public void applyMetrics(MetricsFactory metricsFactory) {
		this.metrics = metricsFactory.createScheduleMetrics(name, nThreads);
	}
}