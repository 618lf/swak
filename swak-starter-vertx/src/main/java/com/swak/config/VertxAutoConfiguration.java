package com.swak.config;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.config.options.MetricsOptionsAutoConfiguration;
import com.swak.config.options.StandardOptionsAutoConfiguration;
import com.swak.config.vertx.ClusterVertxAutoConfiguration;
import com.swak.config.vertx.StandardVerxAutoConfiguration;
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
@ConditionalOnClass(VertxProperties.class)
@EnableConfigurationProperties(VertxProperties.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@Import({ MetricsOptionsAutoConfiguration.class, StandardOptionsAutoConfiguration.class,
		ClusterVertxAutoConfiguration.class, StandardVerxAutoConfiguration.class })
public class VertxAutoConfiguration {

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