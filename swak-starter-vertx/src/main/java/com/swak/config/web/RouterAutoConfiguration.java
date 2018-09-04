package com.swak.config.web;

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

import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.handler.HandlerAdapter;
import com.swak.vertx.handler.ResultHandler;
import com.swak.vertx.handler.converter.Jaxb2RootElementHttpMessageConverter;
import com.swak.vertx.handler.converter.JsonHttpMessageConverter;
import com.swak.vertx.handler.converter.StringHttpMessageConverter;
import com.swak.vertx.handler.formatter.DateFormatter;
import com.swak.vertx.handler.formatter.StringEscapeFormatter;

import io.vertx.ext.web.Router;

/**
 * 路由配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(Router.class)
@EnableConfigurationProperties(VertxProperties.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RouterAutoConfiguration {

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
		registry.addConverter(new DateFormatter());
		registry.addConverter(new StringEscapeFormatter());
	}

	/**
	 * 请求映射器
	 * 
	 * @return
	 */
	@Bean
	public ResultHandler resultHandler() {
		ResultHandler resultHandler = new ResultHandler();
		addConverters(resultHandler);
		return resultHandler;
	}

	protected void addConverters(ResultHandler registry) {
		registry.addConverter(new Jaxb2RootElementHttpMessageConverter());
		registry.addConverter(new StringHttpMessageConverter());
		registry.addConverter(new JsonHttpMessageConverter());
	}

	/**
	 * 请求映射器
	 * 
	 * @return
	 */
	@Bean
	public HandlerAdapter handlerAdapter() {
		return new HandlerAdapter();
	}
}