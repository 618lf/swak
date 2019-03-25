package com.swak.config.vertx;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.swak.vertx.config.AnnotationBean;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.handler.VertxHandler;
import com.swak.vertx.transport.ReactiveServer;

/**
 * vertx 服务器配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(ReactiveServer.class)
@EnableConfigurationProperties(VertxProperties.class)
@Import({ StandardOptionsAutoConfiguration.class, StandardVerxAutoConfiguration.class })
public class VertxAutoConfiguration {

	public VertxAutoConfiguration() {
		APP_LOGGER.debug("Loading Verx");
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
		return new ReactiveServer(annotationBean, properties);
	}
}