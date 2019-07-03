package com.swak.test.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.reactivex.threads.SwakThreadFactory;

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
		SwakThreadFactory threadFactory = new SwakThreadFactory("Test.", true, new AtomicInteger(0), null, 0, null);
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