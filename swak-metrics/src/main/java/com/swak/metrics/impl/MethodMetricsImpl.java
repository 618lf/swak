package com.swak.metrics.impl;

import java.lang.reflect.Method;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.swak.annotation.Counted;
import com.swak.annotation.Timed;
import com.swak.meters.MethodMetrics;
import com.swak.utils.StringUtils;

/**
 * 方法监控
 * 
 * @author lifeng
 */
public class MethodMetricsImpl extends AbstractMetrics implements MethodMetrics<Timer.Context> {

	private MetricRegistry registry;
	private String baseName;
	private Timer time;
	private Counter count;
	private Counted counted;

	protected MethodMetricsImpl(MetricRegistry registry, Method method, String baseName) {
		super(registry, baseName);
		this.initTime(method);
		this.initCount(method);
	}

	private void initTime(Method method) {
		Timed timed = method.getAnnotation(Timed.class);
		if (timed == null) {
			return;
		}
		String name = null;
		if (this.counted.absolute()) {
			name = this.counted.name();
		} else {
			name = baseName + this.counted.name();
		}
		this.time = registry.timer(StringUtils.defaultString(name, baseName));
	}

	private void initCount(Method method) {
		this.counted = method.getAnnotation(Counted.class);
		if (this.counted == null) {
			return;
		}
		String name = null;
		if (this.counted.absolute()) {
			name = this.counted.name();
		} else {
			name = baseName + this.counted.name();
		}
		this.count = registry.counter(StringUtils.defaultString(name, baseName));
	}

	@Override
	public Timer.Context begin() {
		if (count != null && !this.counted.monotonic()) {
			count.inc();
		}
		if (time != null) {
			return time.time();
		}
		return null;
	}

	@Override
	public void end(Context t, boolean succeeded) {
		if (t != null) {
			t.stop();
		}
		if (count != null && !this.counted.monotonic()) {
			count.dec();
		} else if (count != null) {
			count.inc();
		}
	}
}