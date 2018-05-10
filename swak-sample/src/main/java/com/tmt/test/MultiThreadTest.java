package com.tmt.test;

import java.util.concurrent.CountDownLatch;

/**
 * 开发时禁止使用，仅仅作为测试线程安全之用
 * 多线程测试
 * @author lifeng
 */
public class MultiThreadTest {

	/**
	 * 多线程执行的任务，多少个线程
	 * @param run
	 * @param times
	 */
	public static void run(Runnable run, int times, String name) {
		final CountDownLatch countDownLatch = new CountDownLatch(times);
		long start = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
            new Thread(new Runnable() {
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
            System.out.println(name + " spend time " + spendTime + "ms.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
}