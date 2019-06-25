package com.swak.actuator.config.metrics.vertx;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.swak.config.vertx.StandardOptionsAutoConfiguration;
import com.swak.vertx.config.VertxProperties;

import io.vertx.core.VertxOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;

/**
 * 代有监控 options
 * 
 * @author lifeng
 */
@ConditionalOnMissingBean(VertxOptions.class)
@ConditionalOnClass(DropwizardMetricsOptions.class)
@AutoConfigureBefore(StandardOptionsAutoConfiguration.class)
@EnableConfigurationProperties(VertxProperties.class)
public class VerxMetricsOptionsAutoConfiguration extends StandardOptionsAutoConfiguration {

	/**
	 * 构建配置, 添加指标的支持
	 * 
	 * @param properties
	 * @return
	 */
	@Bean
	public VertxOptions vertxOptions(VertxProperties properties) {
		VertxOptions vertxOptions = super.vertxOptions(properties);
		if (properties.isMetricAble()) {
			vertxOptions.setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true));
		}
		return vertxOptions;
	}
}
