package com.swak.metrics.vertx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import com.swak.config.vertx.StandardOptionsAutoConfiguration;
import com.swak.meters.MetricsFactory;
import com.swak.metrics.MetricsAutoConfiguration;

import io.vertx.core.VertxOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;

/**
 * 监控 options
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(DropwizardMetricsOptions.class)
@ConditionalOnBean({ VertxOptions.class, MetricsFactory.class })
@AutoConfigureAfter({ MetricsAutoConfiguration.class, StandardOptionsAutoConfiguration.class })
public class MetricsOptionsAutoConfiguration {

	@Autowired
	public void vertxOptionsMetricsPostProcessor(MetricsFactory metricsFactory, VertxOptions vertxOptions) {
		vertxOptions.setMetricsOptions(
				new DropwizardMetricsOptions().setMetricRegistry(metricsFactory.metricRegistry()).setEnabled(true));
	}
}