package com.swak.metrics.vertx;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.codahale.metrics.MetricRegistry;
import com.swak.config.vertx.StandardOptionsAutoConfiguration;
import com.swak.metrics.MetricsAutoConfiguration;
import com.swak.vertx.config.VertxProperties;

import io.vertx.core.VertxOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;

/**
 * 监控 options
 * 
 * @author lifeng
 */
@ConditionalOnMissingBean(VertxOptions.class)
@ConditionalOnClass(DropwizardMetricsOptions.class)
@AutoConfigureBefore(StandardOptionsAutoConfiguration.class)
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@EnableConfigurationProperties(VertxProperties.class)
public class MetricsOptionsAutoConfiguration extends StandardOptionsAutoConfiguration {

	/**
	 * 添加指标的支持
	 * 
	 * @param properties
	 * @return
	 */
	@Bean
	public VertxOptions vertxOptions(MetricRegistry registry, VertxProperties properties) {
		VertxOptions vertxOptions = super.vertxOptions(properties);
		if (properties.isMetricAble()) {
			vertxOptions.setMetricsOptions(new DropwizardMetricsOptions().setMetricRegistry(registry).setEnabled(true));
		}
		return vertxOptions;
	}
}