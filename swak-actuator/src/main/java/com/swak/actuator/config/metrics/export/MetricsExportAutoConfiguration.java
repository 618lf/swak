package com.swak.actuator.config.metrics.export;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.actuator.config.MetricsAutoConfiguration;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

/**
 * MeterRegistry
 * 
 * @author lifeng
 */
@Configuration
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnBean(Clock.class)
@EnableConfigurationProperties(SimpleProperties.class)
@ConditionalOnMissingBean(MeterRegistry.class)
@ConditionalOnProperty(prefix = Constants.ACTUATOR_METRICS + ".export.simple", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MetricsExportAutoConfiguration {

	/**
	 * 统一使用此 MeterRegistry
	 * 
	 * @param config
	 * @param clock
	 * @return
	 */
	@Bean
	public MeterRegistry simpleMeterRegistry(SimpleProperties config, Clock clock) {
		return new SimpleMeterRegistry(config, clock);
	}
}