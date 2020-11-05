package com.swak.async.persistence.metrics;

import com.swak.meters.MetricsFactory;

/**
 * 指标收集
 * 
 * @author lifeng
 * @date 2020年11月5日 下午11:55:03
 */
public class MetricsCollector {

	public static MetricsCollector instance = new MetricsCollector();

	private MetricsFactory metricsFactory;

	/**
	 * 注册指标统计
	 * 
	 * @param metricsFactory
	 */
	public static void registryMetricsFactory(MetricsFactory metricsFactory) {
		instance.metricsFactory = metricsFactory;
	}

	/**
	 * 获得指标
	 * 
	 * @return
	 */
	public static MetricsFactory getMetricsFactory() {
		return instance.metricsFactory;
	}
}