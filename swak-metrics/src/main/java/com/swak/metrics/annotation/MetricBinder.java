package com.swak.metrics.annotation;

import com.codahale.metrics.MetricRegistry;

/**
 * 绑定到指标收集器
 * 
 * @author lifeng
 */
@FunctionalInterface
public interface MetricBinder {

	/**
	 * 指定收集器
	 * 
	 * @param registry
	 */
	void bindTo(MetricRegistry registry);
}