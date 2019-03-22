package com.swak.test.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import com.swak.reactivex.transport.resources.EventLoopFactory;

/**
 * 开发时禁止使用，仅仅作为测试线程安全之用 多线程测试
 * 
 * @author lifeng
 */
public class MultiThreadTest {

	/**
	 * 多线程执行的任务，多少个线程
	 * 
	 * @param run
	 * @param times
	 */
	public static void run(Runnable run, int threads, String name) {
		EventLoopFactory threadFactory = new EventLoopFactory(true, "Test.", new AtomicLong());
		final CountDownLatch countDownLatch = new CountDownLatch(threads);
		long start = System.currentTimeMillis();
		for (int i = 0; i < threads; i++) {
			threadFactory.newThread(new Runnable() {
				@Override
				public void run() {
					try {
						run.run();
					} finally {
						countDownLatch.countDown();
					}
				}
			}).start();
		}

		try {
			countDownLatch.await();
			final long spendTime = System.currentTimeMillis() - start;
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println(name + " spend time " + spendTime + "ms.");
				}
			}));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}