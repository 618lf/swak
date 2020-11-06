package com.swak.config.jdbc.database;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.meters.MetricsFactory;
import com.swak.persistence.metrics.MetricsCollector;

/**
 * 指标的配置
 * 
 * @author lifeng
 * @date 2020年11月6日 下午2:49:15
 */
public abstract class MetricsConfiguration {

	@Autowired(required = false)
	private MetricsFactory metricsFactory;

	public MetricsConfiguration() {
		if (metricsFactory != null) {
			MetricsCollector.registryMetricsFactory(metricsFactory);
		}
	}
}