package com.swak.actuator.config.metrics.web;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.actuator.config.MetricsAutoConfiguration;
import com.swak.actuator.config.metrics.MetricsProperties;
import com.swak.actuator.config.metrics.export.SimpleMetricsExportAutoConfiguration;

import io.micrometer.core.instrument.MeterRegistry;

@Configuration
@AutoConfigureAfter({ MetricsAutoConfiguration.class,
		SimpleMetricsExportAutoConfiguration.class })
@ConditionalOnBean(MeterRegistry.class)
public class WebMetricsAutoConfiguration {
	
	@Bean
	@ConditionalOnMissingBean(WebTagsProvider.class)
	public WebTagsProvider webTagConfigurer() {
		return (exchange, ex) -> {
			return Arrays.asList(WebFluxTags.method(exchange), WebFluxTags.uri(exchange),
					WebFluxTags.exception(ex), WebFluxTags.status(exchange)); 
		};
	}

	@Bean
	public MetricsWebFilter webfluxMetrics(MeterRegistry registry,
			WebTagsProvider tagConfigurer, MetricsProperties properties) {
		return new MetricsWebFilter(registry, tagConfigurer,
				properties.getRequestsMetricName());
	}
}
