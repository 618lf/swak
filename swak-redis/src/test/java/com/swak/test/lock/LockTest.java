package com.swak.test.lock;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.swak.lock.redis.ConditionRedisLock;
import com.swak.lock.redis.OrderRedisLock;
import com.swak.test.RedisTest;

/**
 * 测试锁
 * 
 * @author lifeng
 */
public class LockTest extends RedisTest {

	Integer count = 0;

	/**
	 * 简单的锁
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void simpleLock() throws InterruptedException {
		Long start_time = System.currentTimeMillis();
		CountDownLatch latch = new CountDownLatch(10000);
		OrderRedisLock lock = new OrderRedisLock("wx");
		for (int i = 0; i < 10000; i++) {
			lock.execute(() -> {
				count++;
				return 1;
			}).thenAccept((v) -> {
				latch.countDown();
			});
		}

		latch.await();
		Long end_time = System.currentTimeMillis();
		System.out.println(count + "; time=" + (end_time - start_time));
	}
	
	/**
	 * 条件 - 简单的锁
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void conditionLock() throws InterruptedException {
		Long start_time = System.currentTimeMillis();
		CountDownLatch latch = new CountDownLatch(10000);
		ConditionRedisLock lock = new ConditionRedisLock("cwx");
		for (int i = 0; i < 10000; i++) {
			lock.execute(() -> {
				count++;
				return 1;
			}).thenAccept((v) -> {
				latch.countDown();
			});
		}

		latch.await();
		Long end_time = System.currentTimeMillis();
		System.out.println(count + "; time=" + (end_time - start_time));
	}
}