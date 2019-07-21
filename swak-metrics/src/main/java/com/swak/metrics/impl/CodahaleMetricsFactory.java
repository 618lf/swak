package com.swak.metrics.impl;

import java.lang.reflect.Method;

import com.codahale.metrics.MetricRegistry;
import com.swak.annotation.Counted;
import com.swak.annotation.Timed;
import com.swak.meters.MethodMetrics;
import com.swak.meters.MetricsFactory;
import com.swak.meters.PoolMetrics;

/**
 * 基于 Codahale 指标创建工厂
 * 
 * @author lifeng
 */
public class CodahaleMetricsFactory implements MetricsFactory {

	private final MetricRegistry registry;

	public CodahaleMetricsFactory(MetricRegistry metricRegistry) {
		this.registry = metricRegistry;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T metricRegistry() {
		return (T) registry;
	}

	@Override
	public PoolMetrics<?> cteatePoolMetrics(String name, int maxSize) {
		return new PoolMetricsImpl(registry, name + "pool", maxSize);
	}

	@Override
	public PoolMetrics<?> createScheduleMetrics(String name, int maxSize) {
		return new ScheduleMetricsImpl(registry, name + "schedule", maxSize);
	}

	@Override
	public MethodMetrics<?> createMethodMetrics(Method method, String name) {
		if (this.applyMethod(method)) {
			return new MethodMetricsImpl(registry, method, name);
		}
		return null;
	}

	private boolean applyMethod(Method method) {
		return method.isAnnotationPresent(Timed.class) || method.isAnnotationPresent(Counted.class);
	}
}