package com.tmt.actuator.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tmt.actuator.endpoint.web.WebEndpointDiscoverer;

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
	
	@Bean
	@ConditionalOnMissingBean(WebEndpointDiscoverer.class)
	public WebEndpointDiscoverer webEndpointDiscoverer() {
		return new WebEndpointDiscoverer(properties.getRootPath(), applicationContext);
	}
}