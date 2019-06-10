package com.swak.rabbit.test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import com.swak.rabbit.RabbitTest;

/**
 * 测试消费者
 * 
 * @author lifeng
 */
public class ConsumeTest extends RabbitTest {

	/**
	 * 单个消费
	 * 
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws InterruptedException
	 */
	@Test
	public void oneConsumeer() throws IOException, TimeoutException, InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		rabbitTemplate.basicConsume(QUEUE, 1, (message) -> {
			System.out.println("收到消息");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		});
		countDownLatch.await();
	}
}
