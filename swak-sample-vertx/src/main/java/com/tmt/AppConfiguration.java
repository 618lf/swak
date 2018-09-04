package com.tmt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.vertx.config.IRouterConfig;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;

/**
 * 项目配置
 * 
 * @author lifeng
 */
@Configuration
public class AppConfiguration {

	/**
	 * 网站自定义的路由配置
	 * 
	 * @return
	 */
	@Bean
	public IRouterConfig routerConfig() {
		return new IRouterConfig() {

			@Override
			public void apply(Router router) {
				router.route().handler(CorsHandler.create("*"));
			}
		};
	}
}