package com.swak.config.vertx;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.protocol.im.ImHandler;
import com.swak.vertx.protocol.im.ImHandlerAdapter;
import com.swak.vertx.protocol.im.ResultHandler;
import com.swak.vertx.protocol.im.converter.HttpMessageConverter;
import com.swak.vertx.protocol.im.converter.Jaxb2RootElementHttpMessageConverter;
import com.swak.vertx.protocol.im.converter.JsonHttpMessageConverter;
import com.swak.vertx.protocol.im.converter.PlainStreamMessageConverter;
import com.swak.vertx.protocol.im.converter.StreamMessageConverter;
import com.swak.vertx.protocol.im.converter.StringHttpMessageConverter;
import com.swak.vertx.transport.server.ReactiveServer;

/**
 * 路由配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ ReactiveServer.class })
@EnableConfigurationProperties(VertxProperties.class)
@Import(FormatterAutoConfiguration.class)
public class ImAutoConfiguration {

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
	 * IM处理器
	 * 
	 * @return
	 */
	@Bean
	public ImHandler webSocketHandler() {
		return new ImHandlerAdapter();
	}
}