package com.swak.config.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import com.swak.async.persistence.metrics.MetricsCollector;
import com.swak.meters.MetricsFactory;

@Configuration
@ConditionalOnClass({ MetricsCollector.class })
@ConditionalOnBean({ MetricsFactory.class })
@AutoConfigureAfter({ MetricsAutoConfiguration.class })
public class AsyncSqlMetricsConfiguration {

	@Autowired
	public void contexteMetricsPostProcessor(MetricsFactory metricsFactory) {
		MetricsCollector.registryMetricsFactory(metricsFactory);
	}
}