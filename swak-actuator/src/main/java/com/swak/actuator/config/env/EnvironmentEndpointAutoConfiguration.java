package com.swak.actuator.config.env;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.swak.actuator.env.EnvironmentEndpoint;

/**
 * 环境
 * @author lifeng
 */
@Configuration
public class EnvironmentEndpointAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public EnvironmentEndpoint environmentEndpoint(Environment environment) {
		EnvironmentEndpoint endpoint = new EnvironmentEndpoint(environment);
		return endpoint;
	}
}