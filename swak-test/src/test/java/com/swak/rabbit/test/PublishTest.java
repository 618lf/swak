package com.swak.rabbit.test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.swak.rabbit.RabbitTest;
import com.swak.rabbit.message.Message;
import com.swak.test.utils.MultiThreadTest;

/**
 * 发布消息的测试
 * 
 * @author lifeng
 */
public class PublishTest extends RabbitTest {

	/**
	 * 单个发送
	 * 
	 * @throws IOException
	 * @throws TimeoutException
	 */
	@Test
	public void onePublisher() throws IOException, TimeoutException {
		rabbitTemplate.basicPublish(EXCHANGE, ROUTING,
				Message.of().setId("1").setPayload("123".getBytes()).build());
	}

	/**
	 * 多个发送
	 * 
	 * @throws IOException
	 * @throws TimeoutException
	 */
	@Test
	public void multiPublisher() throws IOException, TimeoutException {
		AtomicInteger count = new AtomicInteger(0);
		MultiThreadTest.run(() -> {
			while (true) {
				try {
					Message message = Message.of().setId(String.valueOf(count.incrementAndGet()))
							.setPayload("123".getBytes()).build();
					rabbitTemplate.basicPublish(EXCHANGE, ROUTING, message);
					System.out.println("发送消息：" + message.getId());
				} catch (Exception e) {
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, 1, "multi-publisher");
	}
}