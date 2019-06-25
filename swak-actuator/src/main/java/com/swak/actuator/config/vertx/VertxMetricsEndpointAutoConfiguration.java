package com.swak.actuator.config.vertx;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.actuator.vertx.VertxEndpoint;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

@Configuration
public class VertxMetricsEndpointAutoConfiguration {

	/**
	 * 通过 endpoint 来展示指标
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public VertxEndpoint vertxEndpoint(Vertx vertx, Router mainRouter) {
		return new VertxEndpoint(vertx, mainRouter);
	}
}