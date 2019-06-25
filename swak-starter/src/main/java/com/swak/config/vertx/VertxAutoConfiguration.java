package com.swak.config.vertx;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.vertx.config.AnnotationBean;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.handler.VertxHandler;
import com.swak.vertx.transport.server.ReactiveServer;

/**
 * vertx 服务器配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(ReactiveServer.class)
@EnableConfigurationProperties(VertxProperties.class)
public class VertxAutoConfiguration {

	public VertxAutoConfiguration() {
		APP_LOGGER.debug("Loading Vertx");
		System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
	}

	/**
	 * 加载服务器的配置
	 * 
	 * 需要先启动 VertxHandler
	 * 
	 * @return
	 */
	@Bean
	public AnnotationBean annotationBean(VertxHandler vertx) {
		AnnotationBean annotationBean = new AnnotationBean(vertx);
		return annotationBean;
	}

	/**
	 * Http 服务器
	 * 
	 * @return
	 */
	@Bean
	public ReactiveServer httpServer(AnnotationBean annotationBean, VertxProperties properties) {
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
		return new ReactiveServer(annotationBean, properties);
	}
}