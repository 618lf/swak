package com.swak.vertx.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

/**
 * HttpWebHandlerAdapter 配置
 * @author lifeng
 */
@Configuration
public class HttpHandlerAutoConfiguration {

	@Configuration
	public static class AnnotationConfig {

		private ApplicationContext applicationContext;

		public AnnotationConfig(ApplicationContext applicationContext) {
			this.applicationContext = applicationContext;
		}

		@Bean
		public HttpHandler httpHandler() {
			return WebHttpHandlerBuilder.applicationContext(this.applicationContext)
					.build();
		}
	}
}