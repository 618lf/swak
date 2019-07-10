package com.swak.redis.lock;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.swak.lock.AsyncLock;
import com.swak.lock.NoLock;
import com.swak.lock.OnceInvokeLock;
import com.swak.test.utils.MultiThreadTest;

/**
 * 只需执行一次的优化
 * 
 * @author lifeng
 */
public class OnceInvokeLockTest {

	Integer count = 0;

	/**
	 * 条件 - 简单的锁
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void conditionLock() throws InterruptedException {
		Long start_time = System.currentTimeMillis();
		CountDownLatch latch = new CountDownLatch(10000);
		AsyncLock lock = OnceInvokeLock.of(NoLock.of(), 2);
		MultiThreadTest.run(() -> {
			for (int i = 0; i < 1000; i++) {
				lock.execute(() -> {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}
					count++;
					return 1;
				}).thenAccept((v) -> {
					latch.countDown();
				});
			}
		}, 10, "一次锁");
		latch.await();
		Long end_time = System.currentTimeMillis();
		System.out.println("一次锁:" + count + "; time=" + (end_time - start_time));
	}
}