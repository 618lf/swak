package com.swak.rabbit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;

import com.swak.reactivex.transport.resources.EventLoopFactory;

/**
 * 测试 Rabbit
 * 
 * @author lifeng
 */
public class RabbitTest {

	protected String EXCHANGE = "example";
	protected String ROUTING = "lifeng-example";
	protected String QUEUE = "lifeng-example";
	protected RabbitMQTemplate rabbitTemplate;
	protected ExecutorService executor;

	@Before
	public void init() {
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2,
				new EventLoopFactory(true, "RabbitMQ-Consumers-", new AtomicLong()));
		RabbitMQProperties config = new RabbitMQProperties();
		config.setAutomaticRecoveryEnabled(true);
		config.setUser("guest").setPassword("guest").setHost("127.0.0.1").setPort(5672);
		rabbitTemplate = new RabbitMQTemplate(config).setConsumerWorkServiceExecutor(executor)
				.setDaemonFactory(new EventLoopFactory(true, "RabbitMQ-Daemons-", new AtomicLong()));
		rabbitTemplate.exchangeDirectBindQueue(EXCHANGE, ROUTING, QUEUE);
	}
}