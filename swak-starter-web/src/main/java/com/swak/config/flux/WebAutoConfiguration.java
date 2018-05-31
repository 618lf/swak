package com.swak.config.flux;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
@ConditionalOnMissingBean(WebConfigurationSupport.class)
@AutoConfigureAfter({ SecurityAutoConfiguration.class, SystemEventAutoConfiguration.class })
public class WebAutoConfiguration extends WebConfigurationSupport {
	public WebAutoConfiguration() {
		APP_LOGGER.debug("Loading Web Handler");
	}
}