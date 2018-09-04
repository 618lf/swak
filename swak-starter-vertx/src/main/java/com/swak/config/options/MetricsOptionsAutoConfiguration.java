package com.swak.config.options;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.swak.reactivex.transport.TransportMode;
import com.swak.vertx.config.VertxProperties;

import io.vertx.core.VertxOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;

/**
 * 代有监控 options
 * @author lifeng
 */
@ConditionalOnMissingBean(VertxOptions.class)
@ConditionalOnClass(DropwizardMetricsOptions.class)
@EnableConfigurationProperties(VertxProperties.class)
public class MetricsOptionsAutoConfiguration {

	/**
	 * 构建配置
	 * @param properties
	 * @return
	 */
	@Bean
	public VertxOptions vertxOptions(VertxProperties properties) {
		VertxOptions vertxOptions = new VertxOptions();

		// Dropwizard Metrics
		if (properties.isMetricAble()) {
			vertxOptions.setMetricsOptions(
					new DropwizardMetricsOptions().setEnabled(true).setJmxEnabled(true).setJmxDomain("vertx-metrics"));
		}

		// pool config
		if (properties.getMode() == TransportMode.EPOLL) {
			vertxOptions.setPreferNativeTransport(true);
		}
		vertxOptions.setEventLoopPoolSize(properties.getEventLoopPoolSize());
		return vertxOptions;
	}
}
