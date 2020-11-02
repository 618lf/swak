package com.swak.config.metrics;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.impl.StandardMetricsCollector;
import com.swak.config.mq.RabbitMqAutoConfiguration;
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
@AutoConfigureAfter({ MetricsAutoConfiguration.class, RabbitMqAutoConfiguration.class })
public class RabbitMqMetricsConfiguration {

	@Autowired
	public void rabbitMQTemplateMetricsPostProcessor(MetricsFactory metricsFactory,
			Map<String, RabbitMQTemplate> templates) {
		templates.forEach((name, template) -> {
			template.setMetricsCollector(new StandardMetricsCollector(metricsFactory.metricRegistry(), "RabbitMQ"));
		});
	}
}