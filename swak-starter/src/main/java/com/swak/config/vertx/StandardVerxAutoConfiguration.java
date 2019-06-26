package com.swak.config.vertx;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.swak.vertx.config.StandardVertx;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.handler.VertxProxy;

import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;

/**
 * 配置单机版本的 vertx
 * 
 * @author lifeng
 */
@ConditionalOnMissingBean(StandardVertx.class)
@EnableConfigurationProperties(VertxProperties.class)
public class StandardVerxAutoConfiguration {
	
	/**
	 * 配置标准的 vertx
	 * @param properties
	 * @return
	 */
	@Bean
	public VertxProxy vertxBean(VertxOptions vertxOptions, DeliveryOptions deliveryOptions) {
		return new StandardVertx(vertxOptions, deliveryOptions);
	}
}