package com.swak.redis.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.swak.lock.AsyncLock;
import com.swak.lock.NoLock;
import com.swak.lock.OnceInvokeLock;
import com.swak.lock.OrderInvokeLock;
import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.threads.WorkerContext;
import com.swak.redis.RedisTest;
import com.swak.test.utils.MultiThreadTest;

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
	public void noLock() throws InterruptedException {
		Long start_time = System.currentTimeMillis();
		CountDownLatch latch = new CountDownLatch(10000);
		MultiThreadTest.run(() ->{
			for (int i = 0; i < 1000; i++) {
				count++;
				latch.countDown();
			}
		}, 10, "没有锁");
		latch.await();
		Long end_time = System.currentTimeMillis();
		System.out.println("无锁：" + count + "; time=" + (end_time - start_time));
	}
	
	/**
	 * 简单的锁
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void noAyncLock() throws InterruptedException {
		Long start_time = System.currentTimeMillis();
		CountDownLatch latch = new CountDownLatch(10000);
		AsyncLock lock = OrderInvokeLock.of(NoLock.of(), 60);
		MultiThreadTest.run(() ->{
			for (int i = 0; i < 1000; i++) {
				lock.execute(() -> {
					count++;
					return 1;
				}).thenAccept((v) -> {
					latch.countDown();
				});
			}
		}, 10, "单线程无锁");
		latch.await();
		Long end_time = System.currentTimeMillis();
		System.out.println("单线程无锁：" + count + "; time=" + (end_time - start_time));
	}

	/**
	 * 简单的锁
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void simpleAyncLock() throws InterruptedException {
		WorkerContext context = Contexts.createWorkerContext("Test.", 10, true, 2, TimeUnit.SECONDS);
		Long start_time = System.currentTimeMillis();
		CountDownLatch latch = new CountDownLatch(10000);
		AsyncLock lock = OrderInvokeLock.of(StrictRedisLock.of("wx"), 60);
		MultiThreadTest.run(() ->{
			for (int i = 0; i < 1000; i++) {
				lock.execute(() -> {
					count++;
					return 1;
				}).thenAcceptAsync((v) -> {
					latch.countDown();
				}, context);
			}
		}, 10, "全局分布式锁切线程");
		latch.await();
		Long end_time = System.currentTimeMillis();
		System.out.println("全局分布式锁切线程：" + count + "; time=" + (end_time - start_time));
	}

	/**
	 * 简单的锁
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void simpleLock() throws InterruptedException {
		Long start_time = System.currentTimeMillis();
		CountDownLatch latch = new CountDownLatch(10000);
		AsyncLock lock = OrderInvokeLock.of(StrictRedisLock.of("wx"), 60);
		MultiThreadTest.run(() ->{
			for (int i = 0; i < 1000; i++) {
				lock.execute(() -> {
					count++;
					return 1;
				}).thenAccept((v) -> {
					latch.countDown();
				});
			}
		}, 10, "全局分布式锁不切线程");
		latch.await();
		Long end_time = System.currentTimeMillis();
		System.out.println("全局分布式锁不切线程：" + count + "; time=" + (end_time - start_time));
	}
	
	/**
	 * 条件 - 简单的锁
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void conditionLock() throws InterruptedException {
		WorkerContext context = Contexts.createWorkerContext("Test.", 1, true, 2, TimeUnit.SECONDS);
		Long start_time = System.currentTimeMillis();
		CountDownLatch latch = new CountDownLatch(10000);
		AsyncLock lock = OnceInvokeLock.of(StrictRedisLock.of("wx"), 60);
		MultiThreadTest.run(() ->{
			for (int i = 0; i < 1000; i++) {
				lock.execute(() -> {
					count++;
					return 1;
				}).thenAcceptAsync((v) -> {
					latch.countDown();
				}, context);
			}
		}, 10, "一段时间之内只需要执行一次的锁");
		latch.await();
		Long end_time = System.currentTimeMillis();
		System.out.println("一段时间之内只需要执行一次的锁:" + count + "; time=" + (end_time - start_time));
	}
}