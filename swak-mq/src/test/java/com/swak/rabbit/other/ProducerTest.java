package com.swak.rabbit.other;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.swak.test.utils.MultiThreadTest;

public class ProducerTest {
	public static void main(String[] args) throws IOException, TimeoutException {
		String exchangeName = "confirmExchange";
		String queueName = "confirmQueue";
		String routingKey = "confirmRoutingKey";
		String bindingKey = "confirmRoutingKey";
		int count = 100;

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("127.0.0.1");
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setPort(5672);

		Connection connection = factory.newConnection();

		// 两个生产者 - 创建生产者
		MultiThreadTest.run(() -> {
			new Sender(connection, count, exchangeName, queueName, routingKey, bindingKey).run();
		}, 2, "111");
	}
}

class Sender implements ConfirmListener{
	private Connection connection;
	private int count;
	private String exchangeName;
	private String queueName;
	private String routingKey;
	private String bindingKey;
	Channel channel = null;

	public Sender(Connection connection, int count, String exchangeName, String queueName, String routingKey,
			String bindingKey) {
		this.connection = connection;
		this.count = count;
		this.exchangeName = exchangeName;
		this.queueName = queueName;
		this.routingKey = routingKey;
		this.bindingKey = bindingKey;
	}

	public void run() {
		try {
			channel = connection.createChannel();
			// 创建exchange
			channel.exchangeDeclare(exchangeName, "direct", true, false, null);
			// 创建队列
			channel.queueDeclare(queueName, true, false, false, null);
			// 绑定exchange和queue
			channel.queueBind(queueName, exchangeName, bindingKey);
			channel.confirmSelect();
			channel.addConfirmListener(this);
			// 发送持久化消息
			for (int i = 0; i < count; i++) {
				// 第一个参数是exchangeName(默认情况下代理服务器端是存在一个""名字的exchange的,
				// 因此如果不创建exchange的话我们可以直接将该参数设置成"",如果创建了exchange的话
				// 我们需要将该参数设置成创建的exchange的名字),第二个参数是路由键
				channel.basicPublish(exchangeName, routingKey, MessageProperties.PERSISTENT_BASIC,
						("第" + (i + 1) + "条消息").getBytes());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void handleNack(long deliveryTag, boolean multiple) throws IOException {
		System.out.println("nack: deliveryTag = " + deliveryTag + " multiple: " + multiple);
	}

	@Override
	public void handleAck(long deliveryTag, boolean multiple) throws IOException {
		System.out.println(channel.hashCode() + ";ack: deliveryTag = " + deliveryTag + " multiple: " + multiple);
	}
}