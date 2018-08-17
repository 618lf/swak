package com.swak.config;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;

import com.swak.vertx.config.AnnotationBean;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.converter.DateFormatterConverter;
import com.swak.vertx.converter.StringEscapeFormatterConverter;
import com.swak.vertx.handler.HandlerAdapter;
import com.swak.vertx.transport.MainVerticle;
import com.swak.vertx.transport.ReactiveServer;

import io.vertx.core.Vertx;

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
public class VertxAutoConfiguration {

	/**
	 * 创建 Vertx
	 * 
	 * @return
	 */
	@Bean
	public Vertx vertx() {
		return Vertx.vertx();
	}

	/**
	 * 加载注解
	 * 
	 * @return
	 */
	@Bean
	public AnnotationBean annotationBean(Vertx vertx) {
		return new AnnotationBean(vertx);
	}

	/**
	 * 属性编辑器
	 * 
	 * @return
	 */
	@Bean
	public ConversionService conversionService() {
		FormattingConversionService service = new DefaultFormattingConversionService();
		addFormatters(service);
		return service;
	}

	protected void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new DateFormatterConverter());
		registry.addConverter(new StringEscapeFormatterConverter());
	}

	/**
	 * 请求映射器
	 * 
	 * @return
	 */
	@Bean
	public HandlerAdapter routerAdapter() {
		return new HandlerAdapter();
	}

	/**
	 * 启动一个 http 服务器, 通过前面的路由信息启动服务
	 * 
	 * @return
	 */
	@Bean
	public ReactiveServer httpServer(AnnotationBean annotationBean, HandlerAdapter handlerAdapter, VertxProperties properties) {
		MainVerticle mainVerticle = new MainVerticle(annotationBean, handlerAdapter, properties);
		return new ReactiveServer(annotationBean, mainVerticle);
	}
}