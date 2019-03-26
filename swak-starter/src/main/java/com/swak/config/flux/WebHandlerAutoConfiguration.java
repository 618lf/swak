package com.swak.config.flux;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

import com.swak.config.freemarker.FreeMarkerAutoConfiguration;
import com.swak.config.jdbc.DataSourceAutoConfiguration;
import com.swak.config.jdbc.DataSourceTransactionManagerConfiguration;
import com.swak.reactivex.transport.http.server.ReactiveServer;

/**
 * Web 服务配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(ReactiveServer.class)
@ConditionalOnMissingBean(WebConfigurationSupport.class)
@AutoConfigureAfter({ FreeMarkerAutoConfiguration.class, SecurityAutoConfiguration.class,
		DataSourceAutoConfiguration.class, DataSourceTransactionManagerConfiguration.class })
public class WebHandlerAutoConfiguration extends WebConfigurationSupport {
}