package com.swak.config;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.config.flux.WebAutoConfiguration;
import com.swak.reactivex.context.ReactiveWebServerFactory;
import com.swak.reactivex.transport.http.server.HttpServerProperties;

/**
 * 服务器配置
 * 
 * @author lifeng
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 100)
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
@EnableConfigurationProperties(HttpServerProperties.class)
@AutoConfigureAfter({ WebAutoConfiguration.class })
public class HttpServerAutoConfiguration {

	private HttpServerProperties properties;

	public HttpServerAutoConfiguration(HttpServerProperties properties) {
		this.properties = properties;
		APP_LOGGER.debug("Loading Http Server on http://" + properties.getHost() + ":" + properties.getPort());
	}

	@Bean
	public ReactiveWebServerFactory reactiveWebServerFactory() {
		return new ReactiveWebServerFactory(properties);
	}
}