package com.swak.actuator.config;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;

import com.swak.actuator.endpoint.invoke.OperationParameterResoler;
import com.swak.actuator.endpoint.web.WebEndpointDiscoverer;

/**
 * 将 endpoint 映射为 url。
 * 
 * 之后就可以通过 url 来访问 endpoint 中的内容了。
 * 
 * @author lifeng
 */
@Configuration
@EnableConfigurationProperties(WebEndpointProperties.class)
@Import({ VertxEndpointAutoConfiguration.class })
public class WebEndpointAutoConfiguration {

	private final WebEndpointProperties properties;

	public WebEndpointAutoConfiguration(ApplicationContext applicationContext, WebEndpointProperties properties) {
		APP_LOGGER.debug("Loading Endpoint Web Export");
		this.properties = properties;
	}

	/**
	 * 参数转换
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(OperationParameterResoler.class)
	public OperationParameterResoler operationParameterResoler(ConversionService conversionService) {
		return new OperationParameterResoler(conversionService);
	}

	/**
	 * 加载 Endpoint， Endpoint 需要提供配置文件支持
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(WebEndpointDiscoverer.class)
	public WebEndpointDiscoverer webEndpointDiscoverer(OperationParameterResoler operationParameterResoler) {
		return new WebEndpointDiscoverer(properties.getRootPath(), operationParameterResoler);
	}
}