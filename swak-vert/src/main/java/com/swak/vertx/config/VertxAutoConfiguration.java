package com.swak.vertx.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 服务器配置
 * @author lifeng
 */
@Configuration
@EnableConfigurationProperties(HttpServerProperties.class)
public class VertxAutoConfiguration {

	/**
	 * vertx 实例
	 * @return
	 */
	@Bean
	public VertxFactory vertxFactory() {
		return new VertxFactory();
	}
	
	/**
	 * 服务器 实例
	 * @return
	 */
	@Bean
	public VertxHttpServerFactory httpServerFactory(HttpServerProperties properties) {
		return new VertxHttpServerFactory(properties);
	}
}