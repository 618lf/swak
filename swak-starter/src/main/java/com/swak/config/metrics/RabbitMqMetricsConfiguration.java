package com.swak.config.metrics;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.impl.StandardMetricsCollector;
import com.swak.config.customizer.RabbitOptionsCustomizer;
import com.swak.meters.MetricsFactory;
import com.swak.rabbit.RabbitMQTemplate;

/**
 * Mq 启动指标统计
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ RabbitMQTemplate.class })
@ConditionalOnBean({ MetricsFactory.class })
@AutoConfigureAfter({ MetricsAutoConfiguration.class })
public class RabbitMqMetricsConfiguration {

	@Bean
	public RabbitOptionsCustomizer rabbitOptionsCustomizer(MetricsFactory metricsFactory) {
		return (template) -> {
			template.setMetricsCollector(new StandardMetricsCollector(metricsFactory.metricRegistry(), "RabbitMQ"));
		};
	}
}