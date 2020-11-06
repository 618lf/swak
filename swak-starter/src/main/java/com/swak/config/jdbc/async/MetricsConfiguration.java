package com.swak.config.jdbc.async;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.async.persistence.metrics.MetricsCollector;
import com.swak.meters.MetricsFactory;

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