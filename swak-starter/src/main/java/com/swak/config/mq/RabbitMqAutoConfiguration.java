package com.swak.config.mq;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.rabbit.RabbitMQProperties;
import com.swak.rabbit.RabbitMQTemplate;

/**
 * 消息队列的自动化配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ RabbitMQTemplate.class })
@EnableConfigurationProperties(RabbitMQProperties.class)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableMq", matchIfMissing = true)
public class RabbitMqAutoConfiguration {
	
}