package com.swak.actuator.config;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;

import com.swak.actuator.endpoint.invoke.OperationParameterResoler;
import com.swak.actuator.endpoint.web.WebEndpointDiscoverer;
import com.swak.actuator.endpoint.web.WebEndpointHandlerMapping;
import com.swak.actuator.endpoint.web.WebEndpointHandlerRouter;
import com.swak.actuator.endpoint.web.WebEndpointsSupplier;
import com.swak.vertx.handler.HandlerAdapter;

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
@EnableConfigurationProperties(WebEndpointProperties.class)
public class WebEndpointAutoConfiguration {

	private final ApplicationContext applicationContext;
	private final WebEndpointProperties properties;
	
	public WebEndpointAutoConfiguration(ApplicationContext applicationContext,
			WebEndpointProperties properties) {
		APP_LOGGER.debug("Loading Web Endpoint");
		this.applicationContext = applicationContext;
		this.properties = properties;
	}
	
	/**
	 * 参数转换
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(OperationParameterResoler.class)
	public OperationParameterResoler operationParameterResoler(ConversionService conversionService) {
		return new OperationParameterResoler(conversionService);
	}
	
	/**
	 * 加载 Endpoint，
	 * Endpoint 需要提供配置文件支持
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(WebEndpointDiscoverer.class)
	public WebEndpointDiscoverer webEndpointDiscoverer(OperationParameterResoler operationParameterResoler) {
		return new WebEndpointDiscoverer(properties.getRootPath(), applicationContext, operationParameterResoler);
	}
	
	/**
	 * 加载 HandlerMapping
	 * @param webEndpointsSupplier
	 * @param webEndpointProperties
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(name={"com.swak.reactivex.transport.http.server.ReactiveServer"})
	public WebEndpointHandlerMapping webEndpointHandlerMapping(WebEndpointsSupplier webEndpointsSupplier) {
		return new WebEndpointHandlerMapping(webEndpointsSupplier);
	}
	
	/**
	 * 加载 HandlerMapping
	 * @param webEndpointsSupplier
	 * @param webEndpointProperties
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(name={"com.swak.vertx.transport.ReactiveServer"})
	public WebEndpointHandlerRouter webEndpointHandlerRouter(HandlerAdapter handlerAdapter, WebEndpointsSupplier webEndpointsSupplier) {
		return new WebEndpointHandlerRouter(handlerAdapter, webEndpointsSupplier);
	}
}