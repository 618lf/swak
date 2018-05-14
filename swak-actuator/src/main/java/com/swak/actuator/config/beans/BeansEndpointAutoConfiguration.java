package com.swak.actuator.config.beans;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.actuator.beans.BeansEndpoint;

/**
 * 系统bean
 * @author lifeng
 */
@Configuration
public class BeansEndpointAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public BeansEndpoint beansEndpoint(
			ConfigurableApplicationContext applicationContext) {
		return new BeansEndpoint(applicationContext);
	}
}