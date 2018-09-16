package com.swak.config;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.Constants;
import com.swak.booter.AppBooter;

/**
 * 系统配置
 * 
 * @author lifeng
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 200)
@Order(Ordered.HIGHEST_PRECEDENCE + 200)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableBooter", matchIfMissing = true)
public class AppListenerAutoConfiguration {
	
	public AppListenerAutoConfiguration() {
		APP_LOGGER.debug("Loading App Booter");
	}

	@Bean
	public AppBooter appBooter() {
		return new AppBooter();
	}
}