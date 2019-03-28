package com.swak.config.mq;

import static com.swak.Application.APP_LOGGER;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.rabbit.EventBus;
import com.swak.rabbit.RabbitMQProperties;
import com.swak.rabbit.RabbitMQTemplate;
import com.swak.rabbit.retry.RetryStrategy;
import com.swak.reactivex.transport.resources.EventLoopFactory;
import com.swak.reactivex.transport.resources.EventLoops;

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

	@Autowired
	private RabbitMQProperties properties;
	private ExecutorService executor;
	private EventLoopFactory threadFactory;

	public RabbitMqAutoConfiguration() {
		APP_LOGGER.debug("Loading MQ");
	}

	/**
	 * 自动注册消费者
	 * 
	 * @return
	 */
	@Bean
	public RabbitMqPostProcessor rabbitMqPostProcessor() {
		return new RabbitMqPostProcessor();
	}

	/**
	 * 默认的生产者模板， 默认是不需要自动恢复生产者连接
	 * 
	 * @return
	 */
	@Bean
	public RabbitMQTemplate templateForSender() {
		RabbitMQTemplate templateForSender = new RabbitMQTemplate(properties);
		initTemplate(templateForSender, properties.isAutomaticRecoveryEnabled());
		return templateForSender;
	}

	/**
	 * 默认的消费者模板, 是否共享连接， 默认生产者和消费者是分开的
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnProperty(prefix = Constants.RABBITMQ_PREFIX, name = "notShareConnection", matchIfMissing = true)
	public RabbitMQTemplate templateForConsumer() {
		RabbitMQProperties properties = new RabbitMQProperties(this.properties);
		properties.setAutomaticRecoveryEnabled(true);
		RabbitMQTemplate templateForConsumer = new RabbitMQTemplate(properties);
		initTemplate(templateForConsumer, true);
		return templateForConsumer;
	}

	/**
	 * 初始化模板
	 * 
	 * @param template
	 * @param autoRecovery
	 */
	private void initTemplate(RabbitMQTemplate template, boolean autoRecovery) {
		if (autoRecovery) {
			if (threadFactory == null) {
				threadFactory = new EventLoopFactory(true, "RabbitMQ-Daemons-", new AtomicLong());
				executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2, threadFactory);
				EventLoops.register("RabbitMQ-Daemons", executor, () -> {
					if (!executor.isShutdown()) {
						executor.shutdownNow();
					}
				});
			}
			template.setConsumerWorkServiceExecutor(executor).setShutdownExecutor(executor)
					.setTopologyRecoveryExecutor(executor).setDaemonFactory(threadFactory);
		}
	}

	/**
	 * 注册Event Bus
	 * 
	 * @param templateForSender
	 * @param templateForConsumerProvider
	 * @param retryStrategyProvider
	 * @return
	 */
	@Bean
	public EventBus rabbitEventBus(RabbitMQTemplate templateForSender,
			ObjectProvider<RabbitMQTemplate> templateForConsumerProvider,
			ObjectProvider<RabbitMqConfigurationSupport> configurationProvider) {
		RabbitMqConfigurationSupport configurationSupport = configurationProvider.getIfAvailable();
		RetryStrategy retryStrategy = null;
		if (configurationSupport != null && (retryStrategy = configurationSupport.getRetryStrategy()) != null) {
			retryStrategy.bindSender(templateForSender);
		}
		RabbitMQTemplate templateForConsumer = templateForConsumerProvider.getIfAvailable(() -> {
			return templateForSender;
		});
		return new EventBus.Builder().setStrategy(retryStrategy).setTemplateForConsumer(templateForConsumer)
				.setTemplateForSender(templateForSender).setExecutor(configurationSupport.getExecutor()).build();
	}
}