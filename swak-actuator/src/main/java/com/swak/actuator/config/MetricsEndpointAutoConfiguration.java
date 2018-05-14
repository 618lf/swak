package com.swak.actuator.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.actuator.config.metrics.export.SimpleMetricsExportAutoConfiguration;
import com.swak.actuator.metrics.MetricsEndpoint;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
@ConditionalOnClass(Timed.class)
@AutoConfigureAfter({ MetricsAutoConfiguration.class, 
	SimpleMetricsExportAutoConfiguration.class})
public class MetricsEndpointAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public MetricsEndpoint metricsEndpoint(MeterRegistry registry) {
		return new MetricsEndpoint(registry);
	}
}