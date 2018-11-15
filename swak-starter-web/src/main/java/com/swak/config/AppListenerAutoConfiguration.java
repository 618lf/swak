package com.swak.config;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.booter.AppBooter;

/**
 * 系统配置
 * 
 * @author lifeng
 */
@Configuration
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