package com.swak.actuator.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.actuator.endpoint.web.WebEndpointHandlerMapping;
import com.swak.actuator.endpoint.web.WebEndpointsSupplier;
import com.swak.flux.transport.http.server.ReactiveServer;

/**
 * 将 endpoint 映射为 url。
 * 
 * 之后就可以通过 url 来访问 endpoint 中的内容了。
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(ReactiveServer.class)
@AutoConfigureAfter(WebEndpointAutoConfiguration.class)
public class WebFluxEndpointAutoConfiguration {

	/**
	 * 加载 HandlerMapping
	 * 
	 * @param webEndpointsSupplier
	 * @param webEndpointProperties
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public WebEndpointHandlerMapping webEndpointHandlerMapping(WebEndpointsSupplier webEndpointsSupplier) {
		return new WebEndpointHandlerMapping(webEndpointsSupplier);
	}
}
