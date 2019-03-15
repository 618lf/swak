package com.swak.test.lock;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.swak.lock.redis.RedisLock;
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
		RedisLock lock = new RedisLock("wx");
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