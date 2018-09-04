package com.swak.config.vertx;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.swak.vertx.config.VertxBean;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.handler.VertxHandler;

/**
 * 配置单机版本的vertx
 * @author lifeng
 */
@ConditionalOnMissingBean(VertxBean.class)
@EnableConfigurationProperties(VertxProperties.class)
public class StandardVerxAutoConfiguration {

	/**
	 * 配置标准的 vertx
	 * @param properties
	 * @return
	 */
	@Bean
	public VertxHandler vertxBean(VertxProperties properties) {
		return new VertxBean(properties);
	}
}