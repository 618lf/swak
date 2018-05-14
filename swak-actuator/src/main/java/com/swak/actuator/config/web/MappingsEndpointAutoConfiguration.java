package com.swak.actuator.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.actuator.web.MappingsEndpoint;
import com.swak.reactivex.web.DispatcherHandler;

/**
 * Mappings endpoint
 * 
 * @author lifeng
 */
@Configuration
public class MappingsEndpointAutoConfiguration {

	@Bean
	public MappingsEndpoint mappingsEndpoint(DispatcherHandler dispatcherHandler) {
		return new MappingsEndpoint(dispatcherHandler);
	}
}
