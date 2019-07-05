package com.swak.reactivex.threads;

import com.swak.meters.PoolMetrics;

import io.netty.channel.EventLoop;

/**
 * 对EventLoop 进行监控,只能做到对這一步
 * 
 * @author lifeng
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class EventLoopContext extends EventLoopDecorator implements Context {

	private PoolMetrics metrics;

	public EventLoopContext(EventLoop eventLoop) {
		super(eventLoop);
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