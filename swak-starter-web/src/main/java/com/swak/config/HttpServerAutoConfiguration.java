package com.swak.config;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.config.flux.WebHandlerAutoConfiguration;
import com.swak.reactivex.context.ReactiveServer;
import com.swak.reactivex.handler.HttpHandler;
import com.swak.reactivex.transport.http.server.HttpServer;
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
@AutoConfigureAfter({ WebHandlerAutoConfiguration.class })
public class HttpServerAutoConfiguration {

	private HttpServerProperties properties;

	public HttpServerAutoConfiguration(HttpServerProperties properties) {
		this.properties = properties;
		APP_LOGGER.debug("Loading Http Server");
	}

	/**
	 * 构建 Reactive Server ，需要提供 HttpHandler 来处理 http 请求
	 * @param handler
	 * @return
	 */
	@Bean
	public ReactiveServer reactiveServer(HttpHandler handler) {
		// threadCache
		if (!properties.isThreadCache()) {
			System.setProperty("io.netty.allocator.tinyCacheSize", "0");
			System.setProperty("io.netty.allocator.smallCacheSize", "0");
			System.setProperty("io.netty.allocator.normalCacheSize", "0");
			System.setProperty("io.netty.allocator.hugeCacheSize", "0");
		}
		// 真实的服务器，用于提供 http 服务
		HttpServer httpServer = HttpServer.build(properties);
		return new ReactiveServer(httpServer, handler);
	}
}