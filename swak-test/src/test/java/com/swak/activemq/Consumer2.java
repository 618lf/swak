package com.swak.activemq;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public class Consumer2 {

	@Test
	public void send() throws JMSException {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,
				ActiveMQConnectionFactory.DEFAULT_PASSWORD, "tcp://192.168.137.100:61616");
		Connection connection = factory.createConnection();

		// 为啥需要start
		connection.start();

		Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

		// 创建一个队列
		Queue queue = session.createQueue("user");

		// 消息的消费者
		MessageConsumer consumer = session.createConsumer(queue);

		// 阻塞式获取消息
		while (true) {
			System.out.println("等着获取数据...");
			Message message = consumer.receive();
			if (message instanceof TextMessage) {
				TextMessage text = (TextMessage) message;
				System.out.println("2 - 消费者获取的消息：" + text.getText());
			}
			message.acknowledge();
		}
	}
}
