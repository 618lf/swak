package com.swak.actuator.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.actuator.endpoint.web.WebEndpointHandlerRouter;
import com.swak.actuator.endpoint.web.WebEndpointsSupplier;
import com.swak.vertx.handler.HandlerAdapter;
import com.swak.vertx.transport.ReactiveServer;


/**
 * 将 endpoint 映射为 url。
 * 
 * 之后就可以通过 url 来访问 endpoint 中的内容了。
 * 
 * @author lifeng
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@ConditionalOnClass(ReactiveServer.class)
@AutoConfigureAfter(WebEndpointAutoConfiguration.class)
public class VertxEndpointAutoConfiguration {

	/**
	 * 加载 HandlerMapping
	 * @param webEndpointsSupplier
	 * @param webEndpointProperties
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public WebEndpointHandlerRouter webEndpointHandlerRouter(HandlerAdapter handlerAdapter, WebEndpointsSupplier webEndpointsSupplier) {
		return new WebEndpointHandlerRouter(handlerAdapter, webEndpointsSupplier);
	}
}