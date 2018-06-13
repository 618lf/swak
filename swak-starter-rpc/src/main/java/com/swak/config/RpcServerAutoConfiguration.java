package com.swak.config;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.config.rpc.RpcHandlerAutoConfiguration;
import com.swak.reactivex.context.ReactiveServer;
import com.swak.rpc.handler.RpcHandler;
import com.swak.rpc.server.RpcServer;
import com.swak.rpc.server.RpcServerProperties;

/**
 * 服务器配置
 * 
 * @author lifeng
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 100)
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
@EnableConfigurationProperties(RpcServerProperties.class)
@AutoConfigureAfter({ RpcHandlerAutoConfiguration.class })
public class RpcServerAutoConfiguration {

	private RpcServerProperties properties;

	public RpcServerAutoConfiguration(RpcServerProperties properties) {
		this.properties = properties;
		APP_LOGGER.debug("Loading RPC Server on " + properties.getHost() + ":" + properties.getPort());
	}

	/**
	 * 构建 Reactive Server ，需要提供 RpcHandler 来处理 RPC 请求
	 * @param handler
	 * @return
	 */
	@Bean
	public ReactiveServer reactiveServer(RpcHandler handler) {
		// 真实的服务器，用于提供 http 服务
		RpcServer httpServer = RpcServer.build(properties);
		return new ReactiveServer(httpServer, handler);
	}
}