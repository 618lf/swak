package com.swak.config.vertx;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;

import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.protocol.http.HandlerAdapter;
import com.swak.vertx.protocol.http.ResultHandler;
import com.swak.vertx.protocol.http.converter.HttpMessageConverter;
import com.swak.vertx.protocol.http.converter.Jaxb2RootElementHttpMessageConverter;
import com.swak.vertx.protocol.http.converter.JsonHttpMessageConverter;
import com.swak.vertx.protocol.http.converter.PlainStreamMessageConverter;
import com.swak.vertx.protocol.http.converter.StreamMessageConverter;
import com.swak.vertx.protocol.http.converter.StringHttpMessageConverter;
import com.swak.vertx.protocol.http.formatter.DateFormatter;
import com.swak.vertx.protocol.http.formatter.StringEscapeFormatter;
import com.swak.vertx.transport.server.ReactiveServer;

/**
 * 路由配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ ReactiveServer.class })
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
	public ResultHandler resultHandler(ObjectProvider<List<HttpMessageConverter>> converters) {
		ResultHandler resultHandler = new ResultHandler();
		addConverters(resultHandler);
		List<HttpMessageConverter> $converters = converters.getIfAvailable();
		if ($converters != null) {
			$converters.forEach((converter) -> {
				resultHandler.addConverter(converter);
			});
		}
		return resultHandler;
	}

	protected void addConverters(ResultHandler registry) {
		registry.addConverter(new Jaxb2RootElementHttpMessageConverter());
		registry.addConverter(new StringHttpMessageConverter());
		registry.addConverter(new StreamMessageConverter());
		registry.addConverter(new PlainStreamMessageConverter());
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