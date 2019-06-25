package com.swak.actuator.config;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.actuator.config.metrics.export.MetricsExportAutoConfiguration;
import com.swak.actuator.metrics.MetricsEndpoint;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * 将 MeterRegistry 中的内容通过 endpoint 的方式输出
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(Timed.class)
@AutoConfigureAfter({MetricsExportAutoConfiguration.class })
public class MetricsEndpointAutoConfiguration {

	public MetricsEndpointAutoConfiguration() {
		APP_LOGGER.debug("Loading Metrics Endpoint");
	}

	@Bean
	@ConditionalOnMissingBean
	public MetricsEndpoint metricsEndpoint(MeterRegistry registry) {
		return new MetricsEndpoint(registry);
	}
}