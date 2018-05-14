package com.swak.config;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 自动注册 -- com.swak.actuator
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(name = {"com.swak.actuator.endpoint.annotation.Endpoint"})
public class ActuatorAutoConfiguration {
	
	public ActuatorAutoConfiguration() {
		APP_LOGGER.debug("Loading Endpoint Actuator");
	}
	
	/**
	 * 自动扫描 com.swak.actuator 下的包
	 * @author lifeng
	 */
	@ComponentScan({"com.swak.actuator"})
	public static class ActuatorConfiguration {}
}