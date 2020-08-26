package com.swak.config.vertx;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.protocol.http.RouterHandler;
import com.swak.vertx.protocol.im.ImHandler;
import com.swak.vertx.transport.VertxProxy;
import com.swak.vertx.transport.server.ReactiveServer;

/**
 * vertx 服务器配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(ReactiveServer.class)
@EnableConfigurationProperties(VertxProperties.class)
public class VertxServerAutoConfiguration {

	public VertxServerAutoConfiguration() {
		APP_LOGGER.debug("Loading Vertx");
		System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
	}

	/**
	 * 加载服务配置处理器
	 * 
	 * @return
	 */
	@Bean
	public ServiceAnnotationBeanPostProcessor serviceAnnotationBeanPostProcessor() {
		return new ServiceAnnotationBeanPostProcessor();
	}

	/**
	 * 加载依赖配置处理器
	 * 
	 * @return
	 */
	@Bean
	public ReferenceAnnotationBeanPostProcessor referenceAnnotationBeanPostProcessor() {
		return new ReferenceAnnotationBeanPostProcessor();
	}

	/**
	 * 加载路由配置处理器
	 * 
	 * @return
	 */
	@Bean
	public RouterAnnotationBeanProcessor routerAnnotationBeanProcessor() {
		return new RouterAnnotationBeanProcessor();
	}
	
	/**
	 * 加载路由配置处理器
	 * 
	 * @return
	 */
	@Bean
	public ImAnnotationBeanProcessor imAnnotationBeanProcessor() {
		return new ImAnnotationBeanProcessor();
	}

	/**
	 * Http 服务器
	 * 
	 * @return
	 */
	@Bean
	public ReactiveServer httpServer(VertxProxy vertx, RouterHandler routerHandler, ImHandler webSocketHandler,
			VertxProperties properties) {
		// threadCache
		if (!properties.isThreadCache()) {
			System.setProperty("io.netty.allocator.tinyCacheSize", "0");
			System.setProperty("io.netty.allocator.smallCacheSize", "0");
			System.setProperty("io.netty.allocator.normalCacheSize", "0");
		}
		// leakDetection
		if (properties.getLeakDetectionLevel() != null) {
			System.setProperty("io.netty.leakDetection.level", properties.getLeakDetectionLevel().name());
		}
		return new ReactiveServer(vertx, properties);
	}
}