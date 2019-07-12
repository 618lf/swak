package com.swak.rabbit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;

import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.threads.SwakThreadFactory;

/**
 * 测试 Rabbit
 * 
 * @see ttl 过期可以进入死信队列
 * 
 * @author lifeng
 */
public class RabbitTest {

	protected String EXCHANGE = "test2.update";
	protected String ROUTING = "test2.update";
	protected String QUEUE = "test2.update";
	protected RabbitMQTemplate rabbitTemplate;
	protected ExecutorService executor;
	protected EventBus eventbus;

	// dead queue
	// protected String DEAD_EXCHANGE = "DEAD.update";
	// protected String DEAD_ROUTING = "#";
	// protected String DEAD_QUEUE = "DEAD.update";

	@Before
	public void init() {
		executor = Contexts.createWorkerContext("RabbitMQ-Consumers-", Runtime.getRuntime().availableProcessors() * 2,
				false, 2, TimeUnit.SECONDS);
		RabbitMQProperties config = new RabbitMQProperties();
		config.setAutomaticRecoveryEnabled(true);
		config.setUser("guest").setPassword("guest").setHost("127.0.0.1").setPort(5672);
		rabbitTemplate = new RabbitMQTemplate(config).setConsumerWorkServiceExecutor(executor)
				.setDaemonFactory(new SwakThreadFactory("RabbitMQ-Daemons-", true, new AtomicInteger()));

		// // 死信队列
		// rabbitTemplate.exchangeTopicBindQueue(DEAD_EXCHANGE, DEAD_ROUTING,
		// DEAD_QUEUE, null);
		//
		// // 普通队列，消息处理失败进入死信队列
		// Map<String, Object> agruments = Maps.newHashMap();
		// agruments.put("x-dead-letter-exchange", DEAD_EXCHANGE);
		// rabbitTemplate.exchangeDirectBindQueue(EXCHANGE, ROUTING, QUEUE, agruments);

		eventbus = EventBus.builder().setExecutor(executor).setTemplateForConsumer(rabbitTemplate).setTemplateForSender(rabbitTemplate)
				.setApply((t) -> {
					// 普通队列，消息处理失败进入死信队列
//					Map<String, Object> agruments = Maps.newHashMap();
//					agruments.put("x-dead-letter-exchange", Constants.dead_channel);
//					agruments.put("x-dead-letter-routing-key", Constants.dead_channel);
					t.exchangeDirectBindQueue(EXCHANGE, ROUTING, QUEUE, null);
					return true;
				}).build();

		eventbus.init((t) -> {
		});
	}
}