package com.swak.webflux.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.swak.webflux.security.CharacterEncodingFilter;
import com.swak.webflux.security.SecurityFilter;

/**
 * 系统配置
 * @author lifeng
 */
@Configuration
public class AppAutoConfiguration {

	/**
	 * 安全方面的配置
	 * @author lifeng
	 */
	@Configuration
	public static class SecurityConfiguration {
		
		/**
		 * 创建字符过滤filter
		 * @return
		 */
		@Bean
		@Order(1)
		public CharacterEncodingFilter characterEncodingFilter() {
			return new CharacterEncodingFilter();
		}
		
		/**
		 * 创建安全filter
		 * @return
		 */
		@Bean
		@Order(2)
		public SecurityFilter SecurityFilter() {
			return new SecurityFilter();
		}
	}
}