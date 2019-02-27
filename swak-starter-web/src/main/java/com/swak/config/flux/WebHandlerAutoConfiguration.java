package com.swak.config.flux;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

import com.swak.config.freemarker.FreeMarkerAutoConfiguration;
import com.swak.config.jdbc.DataSourceAutoConfiguration;
import com.swak.config.jdbc.DataSourceTransactionManagerConfiguration;

/**
 * Web 服务配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnMissingBean(WebConfigurationSupport.class)
@AutoConfigureAfter({ FreeMarkerAutoConfiguration.class, SecurityAutoConfiguration.class,
		DataSourceAutoConfiguration.class, DataSourceTransactionManagerConfiguration.class })
public class WebHandlerAutoConfiguration extends WebConfigurationSupport {
	public WebHandlerAutoConfiguration() {
		APP_LOGGER.debug("Loading Web Flux");
	}
}