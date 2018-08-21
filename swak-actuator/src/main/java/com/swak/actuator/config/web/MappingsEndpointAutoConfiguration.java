package com.swak.actuator.config.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.actuator.web.MappingsEndpoint;

/**
 * Mappings endpoint
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(name={"com.swak.reactivex.transport.http.server.ReactiveServer"})
public class MappingsEndpointAutoConfiguration {

	@Bean
	public MappingsEndpoint mappingsEndpoint(com.swak.reactivex.web.DispatcherHandler dispatcherHandler) {
		return new MappingsEndpoint(dispatcherHandler);
	}
}