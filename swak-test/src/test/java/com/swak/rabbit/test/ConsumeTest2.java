package com.swak.rabbit.test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import com.swak.rabbit.RabbitTest;
import com.swak.rabbit.message.Message;

/**
 * 测试消费者
 * 
 * @author lifeng
 */
public class ConsumeTest2 extends RabbitTest {

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
			System.out.println("消费者2：收到消息:" + message.getId() + ";Thread:" + Thread.currentThread().getName());
			return this.doHandle(message);
		});
		countDownLatch.await();
	}

	private CompletableFuture<Void> doHandle(Message message) {
		return CompletableFuture.runAsync(() -> {
			System.out.println("处理消息;Thread:" + Thread.currentThread().getName());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		});
	}
}
