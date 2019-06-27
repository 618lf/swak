package com.swak.reactivex.threads;

import com.swak.meters.PoolMetrics;

import io.netty.channel.EventLoop;

/**
 * 对EventLoop 进行监控
 * 
 * @author lifeng
 */
public class EventLoopContext extends EventLoopDecorator implements Context {

	private PoolMetrics metrics;

	public EventLoopContext(EventLoop eventLoop, PoolMetrics metrics) {
		super(eventLoop);
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