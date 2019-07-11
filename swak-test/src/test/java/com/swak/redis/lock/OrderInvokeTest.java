package com.swak.redis.lock;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.swak.lock.AsyncLock;
import com.swak.lock.NoLock;
import com.swak.lock.OrderInvokeLock;
import com.swak.test.utils.MultiThreadTest;

public class OrderInvokeTest {

	Integer count = 0;
	
	/**
	 * 条件 - 简单的锁
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void syncLock() throws InterruptedException {
		Long start_time = System.currentTimeMillis();
		CountDownLatch latch = new CountDownLatch(10000);
		Object lock = new Object();
		MultiThreadTest.run(() -> {
			for (int i = 0; i < 100; i++) {
				synchronized (lock) {
					count++;
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
					}
					latch.countDown();
				}
			}
		}, 10, "互斥锁");
		latch.await();
		Long end_time = System.currentTimeMillis();
		System.out.println("互斥锁:" + count + "; time=" + (end_time - start_time));
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
		AsyncLock lock = OrderInvokeLock.of(NoLock.of(), 2);
		MultiThreadTest.run(() -> {
			for (int i = 0; i < 100; i++) {
				lock.execute(() -> {
					count++;
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
					}
					return 1;
				}).thenAccept((v) -> {
					latch.countDown();
				});
			}
		}, 10, "顺序锁");
		latch.await();
		Long end_time = System.currentTimeMillis();
		System.out.println("顺序锁:" + count + "; time=" + (end_time - start_time));
	}
}
