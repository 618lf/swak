package com.swak.activemq;

import java.util.concurrent.CountDownLatch;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.junit.Test;

public class Consumer3 {

	@Test
	public void send() throws JMSException, InterruptedException {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,
				ActiveMQConnectionFactory.DEFAULT_PASSWORD, "tcp://192.168.137.100:61616");
		ActiveMQPrefetchPolicy policy = new ActiveMQPrefetchPolicy();
		policy.setQueuePrefetch(1);
		factory.setPrefetchPolicy(policy);
		ActiveMQConnection connection = (ActiveMQConnection) factory.createConnection();
		connection.setPrefetchPolicy(policy);

		// 为啥需要start
		connection.start();

		Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

		// 创建一个队列
		Queue queue = session.createQueue("user");

		// 消息的消费者
		MessageConsumer consumer = session.createConsumer(queue);

		// 通过回调的方式消费
		consumer.setMessageListener((message) -> {
			if (message instanceof TextMessage) {
				TextMessage text = (TextMessage) message;
				try {
					System.out.println("3 - 消费者获取的消息：" + text.getText());
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
		new CountDownLatch(1).await();
	}
}
