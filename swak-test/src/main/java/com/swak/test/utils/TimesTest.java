package com.swak.test.utils;

/**
 * 执行时间的测试
 * 
 * @author lifeng
 * @date 2020年6月1日 上午10:08:59
 */
public class TimesTest {

	/**
	 * 多线程执行的任务，多少个线程
	 * 
	 * @param run
	 * @param times
	 */
	public static void run(Runnable run, String name) {
		long start = System.currentTimeMillis();
		run.run();
		final long spendTime = System.currentTimeMillis() - start;
		System.out.println(name + " spend time " + spendTime + "ms.");
	}

}