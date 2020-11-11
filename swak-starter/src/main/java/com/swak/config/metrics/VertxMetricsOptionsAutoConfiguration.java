package com.swak.config.metrics;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.config.customizer.VertxOptionsCustomizer;
import com.swak.meters.MetricsFactory;

import io.vertx.core.VertxOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;

/**
 * 监控 options
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ DropwizardMetricsOptions.class, VertxOptions.class })
@ConditionalOnBean({ MetricsFactory.class })
@AutoConfigureAfter({ MetricsAutoConfiguration.class })
@ConditionalOnProperty(prefix = Constants.ACTUATOR_METRICS, name = "vertx.open", matchIfMissing = true)
public class VertxMetricsOptionsAutoConfiguration {

	@Bean
	public VertxOptionsCustomizer vertxoptionsCustomizer(MetricsFactory metricsFactory) {
		return (vertxOptions) -> {
			vertxOptions.setMetricsOptions(
					new DropwizardMetricsOptions().setMetricRegistry(metricsFactory.metricRegistry()).setEnabled(true));
		};
	}
}