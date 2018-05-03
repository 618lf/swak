package com.swak.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.swak.common.utils.SpringContextHolder;
import com.swak.reactivex.booter.AppBooter;
import com.swak.reactivex.server.HttpServerProperties;
import com.swak.reactivex.server.ReactiveWebServerFactory;

/**
 * 系统配置
 * 
 * @author lifeng
 */
@Configuration
public class AppAutoConfiguration {
	
	/**
	 * 基础服务
	 * 
	 * @return
	 */
	@Order(1)
	@Bean
	public SpringContextHolder springContextHolder() {
		return new SpringContextHolder();
	}

	/**
	 * 安全配置
	 * 
	 * @author lifeng
	 */
	@Order(2)
	@Configuration
	public static class SecurityConfiguration {

	}

	/**
	 * Web 服务配置
	 * 
	 * @author lifeng
	 */
	@Order(3)
	@Configuration
	@ConditionalOnMissingBean(WebConfigurationSupport.class)
	public static class WebAutoConfiguration extends WebConfigurationSupport {
	}

	/**
	 * 服务器配置
	 * 
	 * @author lifeng
	 */
	@Order(4)
	@Configuration
	@EnableConfigurationProperties(HttpServerProperties.class)
	public static class WebServerAutoConfiguration {

		private HttpServerProperties properties;

		public WebServerAutoConfiguration(HttpServerProperties properties) {
			this.properties = properties;
		}

		@Bean
		public ReactiveWebServerFactory reactiveWebServerFactory() {
			return new ReactiveWebServerFactory(properties);
		}
	}
	
	/**
	 * 系统服务
	 * @author lifeng
	 */
	@Order(5)
	@Configuration
	public static class AppListenerConfig {

		@Bean
		public AppBooter appBooter() {
			return new AppBooter();
		}
	}
}