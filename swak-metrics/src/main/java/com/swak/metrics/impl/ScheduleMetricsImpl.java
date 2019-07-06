package com.swak.metrics.impl;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.RatioGauge;
import com.codahale.metrics.Timer;
import com.swak.meters.PoolMetrics;

/**
 * 针对定时任务做的性能监控
 * 没有队列等待的这一块，因为是延迟队列，任务可以循环执行
 * 只需要監控，當前正在執行的任務。
 * @author lifeng
 */
public class ScheduleMetricsImpl extends AbstractMetrics implements PoolMetrics<Timer.Context> {

	private final Timer usage;
	private Counter inUse;

	public ScheduleMetricsImpl(MetricRegistry registry, String baseName, int maxSize) {
		super(registry, baseName);
		this.usage = timer("usage");
		this.inUse = counter("in-use");
		if (maxSize > 0) {
			RatioGauge gauge = new RatioGauge() {
				@Override
				protected Ratio getRatio() {
					return Ratio.of(inUse.getCount(), maxSize);
				}
			};
			gauge(gauge, "pool-ratio");
			gauge(() -> maxSize, "max-pool-size");
		}
	}

	@Override
	public Timer.Context submitted() {
		return null;
	}

	@Override
	public void rejected(Timer.Context context) {
		context.stop();
	}

	@Override
	public Timer.Context begin(Timer.Context context) {
		inUse.inc();
		return usage.time();
	}

	@Override
	public void end(Timer.Context context, boolean succeeded) {
		inUse.dec();
		context.stop();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void close() {
		removeAll();
	}
}