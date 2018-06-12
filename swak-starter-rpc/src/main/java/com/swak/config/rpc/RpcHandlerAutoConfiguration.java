package com.swak.config.rpc;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Web 服务配置
 * 
 * @author lifeng
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 20)
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
@ConditionalOnMissingBean(RpcConfigurationSupport.class)
public class RpcHandlerAutoConfiguration extends RpcConfigurationSupport {
	public RpcHandlerAutoConfiguration() {
		APP_LOGGER.debug("Loading Rpc Handler");
	}
}