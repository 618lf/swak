package com.swak.config.rocketmq;

import org.apache.rocketmq.client.MQAdmin;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import com.swak.rocketmq.EventBus;
import com.swak.rocketmq.RocketMQProperties;
import com.swak.rocketmq.RocketMQTemplate;

/**
 * RocketMq 配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ MQAdmin.class })
public class RocketMqAutoConfiguration {

	/**
	 * RocketMq 消费者，事务监听处理器
	 * 
	 * @return
	 */
	@Bean
	public RocketMqPostProcessor rocketMqPostProcessor() {
		return new RocketMqPostProcessor();
	}

	/**
	 * 默认的生产者
	 * 
	 * @param rocketMQProperties
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(DefaultMQProducer.class)
	public DefaultMQProducer defaultMQProducer(RocketMQProperties rocketMQProperties) {
		RocketMQProperties.Producer producerConfig = rocketMQProperties.getProducer();
		String nameServer = rocketMQProperties.getNameServer();
		String groupName = producerConfig.getGroup();
		Assert.hasText(nameServer, "[spring.rocketmq.name-server] must not be null");
		Assert.hasText(groupName, "[spring.rocketmq.producer.group] must not be null");

		DefaultMQProducer producer = new DefaultMQProducer(groupName);
		producer.setNamesrvAddr(nameServer);
		producer.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
		producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
		producer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
		producer.setMaxMessageSize(producerConfig.getMaxMessageSize());
		producer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
		producer.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());

		return producer;
	}

	/**
	 * 消息发送模板
	 * 
	 * @param mqProducer
	 * @return
	 */
	@Bean(destroyMethod = "destroy")
	@ConditionalOnBean(DefaultMQProducer.class)
	@ConditionalOnMissingBean(RocketMQTemplate.class)
	public RocketMQTemplate rocketMQTemplate(DefaultMQProducer mqProducer) {
		RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
		rocketMQTemplate.setProducer(mqProducer);
		return rocketMQTemplate;
	}

	/**
	 * 消息发送模板
	 * 
	 * @param mqProducer
	 * @return
	 */
	@Bean(destroyMethod = "destroy")
	@ConditionalOnBean(RocketMQTemplate.class)
	public EventBus eventBus(RocketMQTemplate template) {
		return EventBus.builder().setTemplateForSender(template).setApply((t) -> true).build();
	}
}