package com.swak.rabbit;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;

import com.swak.reactivex.transport.resources.EventLoopFactory;
import com.swak.utils.Maps;

/**
 * 测试 Rabbit
 * 
 * @see ttl 过期可以进入死信队列
 * 
 * @author lifeng
 */
public class RabbitTest {

	protected String EXCHANGE = "test.update";
	protected String ROUTING = "test.update";
	protected String QUEUE = "test.update";
	protected RabbitMQTemplate rabbitTemplate;
	protected ExecutorService executor;
	
	// dead queue
	protected String DEAD_EXCHANGE = "DEAD.update";
	protected String DEAD_ROUTING = "#";
	protected String DEAD_QUEUE = "DEAD.update";

	@Before
	public void init() {
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2,
				new EventLoopFactory(true, "RabbitMQ-Consumers-", new AtomicLong()));
		RabbitMQProperties config = new RabbitMQProperties();
		config.setAutomaticRecoveryEnabled(true);
		config.setUser("guest").setPassword("guest").setHost("127.0.0.1").setPort(5672);
		rabbitTemplate = new RabbitMQTemplate(config).setConsumerWorkServiceExecutor(executor)
				.setDaemonFactory(new EventLoopFactory(true, "RabbitMQ-Daemons-", new AtomicLong()));
		
		// 死信队列
		rabbitTemplate.exchangeTopicBindQueue(DEAD_EXCHANGE, DEAD_ROUTING, DEAD_QUEUE, null);
		
		// 普通队列，消息处理失败进入死信队列
		Map<String, Object> agruments = Maps.newHashMap();
		agruments.put("x-dead-letter-exchange", DEAD_EXCHANGE);
		rabbitTemplate.exchangeDirectBindQueue(EXCHANGE, ROUTING, QUEUE, agruments);
		
	}
}