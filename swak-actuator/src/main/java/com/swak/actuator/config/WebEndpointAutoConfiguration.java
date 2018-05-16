package com.swak.actuator.config;

import java.util.Collection;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

import com.swak.actuator.endpoint.invoke.OperationParameterResoler;
import com.swak.actuator.endpoint.web.ExposableWebEndpoint;
import com.swak.actuator.endpoint.web.WebEndpointDiscoverer;
import com.swak.actuator.endpoint.web.WebEndpointHandlerMapping;
import com.swak.actuator.endpoint.web.WebEndpointsSupplier;

/**
 * Endpoint 自动配置
 * @author lifeng
 */
@Configuration
@EnableConfigurationProperties(WebEndpointProperties.class)
public class WebEndpointAutoConfiguration {

	private final ApplicationContext applicationContext;
	private final WebEndpointProperties properties;
	
	public WebEndpointAutoConfiguration(ApplicationContext applicationContext,
			WebEndpointProperties properties) {
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
	 * 加载 Endpoint
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
	public WebEndpointHandlerMapping webEndpointHandlerMapping(WebEndpointsSupplier webEndpointsSupplier) {
		Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier
				.getEndpoints();
		return new WebEndpointHandlerMapping(webEndpoints);
	}
}