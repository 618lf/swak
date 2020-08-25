package com.sample;

import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.config.mq.RabbitMqConfigurationSupport;
import com.swak.config.vertx.SecurityConfigurationSupport;
import com.swak.rabbit.retry.MemoryRetryStrategy;
import com.swak.utils.Maps;
import com.swak.utils.Sets;
import com.swak.vertx.config.IRouterConfig;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.security.SecurityHandler;

import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

/**
 * 项目配置
 * 
 * @author lifeng
 */
@Configuration
public class AppConfiguration {

	/**
	 * 基于内存的重试规则
	 * 
	 * @return
	 */
	@Bean
	public MemoryRetryStrategy memoryRetryStrategy() {
		return new MemoryRetryStrategy();
	}

	/**
	 * 定义消息队列的初始化, 以及初始化队列
	 * 
	 * @return
	 */
	@Bean
	public RabbitMqConfigurationSupport rabbitMqConfiguration(MemoryRetryStrategy retryStrategy) {
		RabbitMqConfigurationSupport rabbitMqConfiguration = new RabbitMqConfigurationSupport();
		rabbitMqConfiguration.setRetryStrategy(retryStrategy);
		rabbitMqConfiguration.setApply((sender) -> {
			Map<String, Object> agruments = Maps.newHashMap();
			agruments.put("x-dead-letter-exchange", com.swak.rabbit.Constants.dead_channel);
			agruments.put("x-dead-letter-routing-key", com.swak.rabbit.Constants.dead_channel);
			sender.exchangeDirectBindQueue("swak.test.goods", "swak.test.goods", "swak.test.goods", agruments);
			return true;
		});
		return rabbitMqConfiguration;
	}

	/**
	 * 安全过滤
	 * 
	 * @return
	 */
	@Bean
	public SecurityConfigurationSupport securitySupport() {
		// 权限配置
		SecurityConfigurationSupport support = new SecurityConfigurationSupport();
		support.definition("/api/login=anno").definition("/api/logout=anno").definition("/api/reqister=anno")
				.definition("/api/goods=anno").definition("/api/param=anno").definition("/api/test=anno")
				.definition("/api/user/=user").definition("/api/manage/=user, role[admin]").definition("/=user");
		return support;
	}

	/**
	 * 网站自定义的路由配置， 可以配置多个
	 * 
	 * @return
	 */
	@Bean
	public IRouterConfig routerConfig(SecurityHandler securityHandler, VertxProperties properties) {
		return (vertx, router) -> {
			Set<String> headers = Sets.newHashSet();
			headers.add("X-Requested-With");
			headers.add(properties.getJwtTokenName());
			router.route().handler(CorsHandler.create("*").allowedHeaders(headers));
			router.route()
					.handler(BodyHandler.create(properties.getUploadDirectory()).setBodyLimit(properties.getBodyLimit())
							.setDeleteUploadedFilesOnEnd(properties.isDeleteUploadedFilesOnEnd()));
			// router.route().handler(StaticHandler.create("static")); //
			// 使用内部阻塞线程处理和work线程不是同一种线程
			router.route().handler(securityHandler);
		};
	}
}