package com.swak.config.mq;

import static com.swak.Application.APP_LOGGER;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.threads.SwakThreadFactory;

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
	private ThreadFactory threadFactory;

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
		if (threadFactory == null) {
			threadFactory = new SwakThreadFactory("RabbitMQ-Daemons-", true, new AtomicInteger());
		}
		if (autoRecovery) {
			if (executor == null) {
				executor = Contexts.createWorkerContext("RabbitMQ-Daemons-",
						Runtime.getRuntime().availableProcessors() * 2, true, 2, TimeUnit.SECONDS);
			}
			template.setConsumerWorkServiceExecutor(executor).setShutdownExecutor(null)
					.setTopologyRecoveryExecutor(executor);
		}
		template.setDaemonFactory(threadFactory);
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
	public EventBus rabbitEventBus(@Qualifier("templateForSender") RabbitMQTemplate templateForSender,
			@Qualifier("templateForConsumer") ObjectProvider<RabbitMQTemplate> templateForConsumerProvider,
			ObjectProvider<RabbitMqConfigurationSupport> configurationProvider) {
		RabbitMqConfigurationSupport configurationSupport = configurationProvider.getIfAvailable();
		RetryStrategy retryStrategy = null;
		Function<RabbitMQTemplate, Boolean> apply = null;
		if (configurationSupport != null) {
			retryStrategy = configurationSupport.getRetryStrategy();
			apply = configurationSupport.getApply();
		}
		if (retryStrategy != null) {
			retryStrategy.bindSender(templateForSender);
		}
		if (apply == null) {
			apply = (t) -> true;
		}
		RabbitMQTemplate templateForConsumer = templateForConsumerProvider.getIfAvailable(() -> {
			return templateForSender;
		});
		return EventBus.builder().setStrategy(retryStrategy).setTemplateForConsumer(templateForConsumer)
				.setTemplateForSender(templateForSender).setApply(apply).setExecutor(executor).build();
	}
}