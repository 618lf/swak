package com.swak.config.flux;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

import com.swak.config.jdbc.DataBaseAutoConfiguration;
import com.swak.config.motan.MotanAutoConfiguration;

/**
 * Web 服务配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnMissingBean(WebConfigurationSupport.class)
@AutoConfigureAfter({SecurityAutoConfiguration.class, DataBaseAutoConfiguration.class, MotanAutoConfiguration.class})
public class WebHandlerAutoConfiguration extends WebConfigurationSupport {
	public WebHandlerAutoConfiguration() {
		APP_LOGGER.debug("Loading Web Flux");
	}
}