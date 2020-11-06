package com.swak.metrics.impl;

import java.lang.reflect.Method;

import com.codahale.metrics.MetricRegistry;
import com.swak.annotation.Counted;
import com.swak.annotation.Timed;
import com.swak.meters.MethodMetrics;
import com.swak.meters.MetricsFactory;
import com.swak.meters.PoolMetrics;
import com.swak.meters.SqlMetrics;

/**
 * 基于 Codahale 指标创建工厂
 * 
 * @author lifeng
 */
public class CodahaleMetricsFactory implements MetricsFactory {

	private final MetricRegistry registry;

	private boolean methodOpen;
	private boolean methodCollectAll;
	private boolean poolOpen;
	private boolean sqlOpen;

	public CodahaleMetricsFactory(MetricRegistry metricRegistry) {
		this.registry = metricRegistry;
	}

	public CodahaleMetricsFactory setMethodOpen(boolean methodOpen) {
		this.methodOpen = methodOpen;
		return this;
	}

	public CodahaleMetricsFactory setMethodCollectAll(boolean methodCollectAll) {
		this.methodCollectAll = methodCollectAll;
		return this;
	}

	public CodahaleMetricsFactory setPoolOpen(boolean poolOpen) {
		this.poolOpen = poolOpen;
		return this;
	}

	public CodahaleMetricsFactory setSqlOpen(boolean sqlOpen) {
		this.sqlOpen = sqlOpen;
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T metricRegistry() {
		return (T) registry;
	}

	@Override
	public PoolMetrics<?> createPoolMetrics(String name, int maxSize) {
		return this.poolOpen ? new PoolMetricsImpl(registry, name + "pool", maxSize) : null;
	}

	@Override
	public PoolMetrics<?> createScheduleMetrics(String name, int maxSize) {
		return this.poolOpen ? new ScheduleMetricsImpl(registry, name + "schedule", maxSize) : null;
	}

	@Override
	public MethodMetrics<?> createMethodMetrics(Method method, String name) {
		if (this.applyMethod(method)) {
			return new MethodMetricsImpl(registry, method, name);
		}
		return null;
	}

	private boolean applyMethod(Method method) {
		return this.methodOpen && (this.methodCollectAll || method.isAnnotationPresent(Timed.class)
				|| method.isAnnotationPresent(Counted.class));
	}

	@Override
	public SqlMetrics<?> createSqlMetrics(String sql) {
		return this.sqlOpen ? SqlMetricsImpl.get(registry, sql) : null;
	}
}