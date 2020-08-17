package com.swak.meters;

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
	void bindTo(MetricsFactory metricsFactory);
}