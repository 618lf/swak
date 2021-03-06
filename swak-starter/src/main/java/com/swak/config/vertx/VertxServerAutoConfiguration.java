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
		APP_LOGGER.debug("Loading Reactive Server");
	}

	/**
	 * 加载服务配置处理器
	 * 
	 * @return
	 */
	@Bean
	public FluxServiceAnnotationBeanPostProcessor fluxServiceAnnotationBeanPostProcessor() {
		return new FluxServiceAnnotationBeanPostProcessor();
	}

	/**
	 * 加载依赖配置处理器
	 * 
	 * @return
	 */
	@Bean
	public FluxReferenceAnnotationBeanPostProcessor fluxReferenceAnnotationBeanPostProcessor() {
		return new FluxReferenceAnnotationBeanPostProcessor();
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
	 * 响应式服务器
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