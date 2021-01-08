package com.swak.activemq;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public class Producer {

	@Test
	public void send() throws JMSException {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,
				ActiveMQConnectionFactory.DEFAULT_PASSWORD, "tcp://192.168.137.100:61616");
		Connection connection = factory.createConnection();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// 创建一个队列
		Queue queue = session.createQueue("user");

		// 消息的生产者
		MessageProducer producer = session.createProducer(queue);
		
		TextMessage message = session.createTextMessage("Hello World!");
		
		// 发送消息
		producer.send(message);
		
		connection.close();
		
		System.out.println("消息发送成功！");
	}
}
