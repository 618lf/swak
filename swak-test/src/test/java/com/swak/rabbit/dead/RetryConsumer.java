package com.swak.rabbit.dead;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import com.swak.rabbit.Constants;
import com.swak.rabbit.RabbitTest;

/**
 * 消费重试记录
 * 
 * @author lifeng
 */
public class RetryConsumer extends RabbitTest {

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
		rabbitTemplate.basicConsume(Constants.retry_channel, 1, (message) -> {
			System.out.println("收到消息");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			throw new RuntimeException("处理失败，进入死信队列");
		});
		countDownLatch.await();
	}
}
