package com.swak.metrics.mq;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.impl.StandardMetricsCollector;
import com.swak.config.mq.RabbitMqAutoConfiguration;
import com.swak.meters.MetricsFactory;
import com.swak.metrics.MetricsAutoConfiguration;
import com.swak.rabbit.RabbitMQTemplate;

/**
 * 如果是 HikariDataSource 则启用
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
			template.setMetricsCollector(new StandardMetricsCollector(metricsFactory.metricRegistry(), "Rabbit."));
		});
	}
}