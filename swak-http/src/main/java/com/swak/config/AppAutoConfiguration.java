package com.swak.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.common.utils.SpringContextHolder;
import com.swak.reactivex.server.HttpServerProperties;
import com.swak.reactivex.server.ReactiveWebServerFactory;

/**
 * 系统配置
 * @author lifeng
 */
@Configuration
public class AppAutoConfiguration {

	/**
	 * 基础服务
	 * @return
	 */
	@Bean
	public SpringContextHolder springContextHolder() {
		return new SpringContextHolder();
	}
	
	/**
	 * 安全配置
	 * @author lifeng
	 */
	@Configuration
	public static class SecurityConfiguration {
		
	}
	
	/**
	 * Web 服务配置
	 * @author lifeng
	 */
	@Configuration
	public static class WebAutoConfiguration extends WebConfigurationSupport{}
	
	/**
	 * 服务器配置
	 * @author lifeng
	 */
	@Configuration
	@EnableConfigurationProperties(HttpServerProperties.class)
	public static class HttpServerConfiguration {
		
		private HttpServerProperties properties;
		
		public HttpServerConfiguration(HttpServerProperties properties) {
			this.properties = properties;
		}
		
		@Bean
		public ReactiveWebServerFactory reactiveWebServerFactory() {
			return new ReactiveWebServerFactory(properties);
		}
	}
}