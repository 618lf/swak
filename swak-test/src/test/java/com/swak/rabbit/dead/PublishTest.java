package com.swak.rabbit.dead;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import com.swak.rabbit.RabbitTest;
import com.swak.rabbit.message.Message;

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
				Message.builder().setId("1").setPayload("123".getBytes()).build());
	}
}