package com.swak.config.vertx;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.protocol.http.ResultHandler;
import com.swak.vertx.protocol.http.RouterHandler;
import com.swak.vertx.protocol.http.RouterHandlerAdapter;
import com.swak.vertx.protocol.http.converter.HttpMessageConverter;
import com.swak.vertx.protocol.http.converter.Jaxb2RootElementHttpMessageConverter;
import com.swak.vertx.protocol.http.converter.JsonHttpMessageConverter;
import com.swak.vertx.protocol.http.converter.PlainStreamMessageConverter;
import com.swak.vertx.protocol.http.converter.StreamMessageConverter;
import com.swak.vertx.protocol.http.converter.StringHttpMessageConverter;
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
public class RouterAutoConfiguration {

	/**
	 * 请求映射器
	 * 
	 * @return
	 */
	@Bean
	public ResultHandler routerResultHandler(ObjectProvider<List<HttpMessageConverter>> converters) {
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
	 * 请求处理器
	 * 
	 * @return
	 */
	@Bean
	public RouterHandler routerHandler() {
		return new RouterHandlerAdapter();
	}
}