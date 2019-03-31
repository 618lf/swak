package com.swak.config.vertx;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;

import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.handler.HandlerAdapter;
import com.swak.vertx.handler.ResultHandler;
import com.swak.vertx.handler.converter.Jaxb2RootElementHttpMessageConverter;
import com.swak.vertx.handler.converter.JsonHttpMessageConverter;
import com.swak.vertx.handler.converter.StreamMessageConverter;
import com.swak.vertx.handler.converter.StringHttpMessageConverter;
import com.swak.vertx.handler.formatter.DateFormatter;
import com.swak.vertx.handler.formatter.StringEscapeFormatter;
import com.swak.vertx.transport.server.ReactiveServer;

/**
 * 路由配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ReactiveServer.class})
@EnableConfigurationProperties(VertxProperties.class)
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
		registry.addConverter(new StreamMessageConverter());
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