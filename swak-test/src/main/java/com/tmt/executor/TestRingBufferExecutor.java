package com.tmt.executor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.http.Executeable;
import com.swak.http.pool.RingBufferExecutor;

/**
 * 10000000 次 需要 与线程有关，线程越多越慢
 * @author lifeng
 */
public class TestRingBufferExecutor {

	public static void main(String[] args) throws InterruptedException {
		String definitions = "DEFAULT = 30";
		Executeable executor = new RingBufferExecutor();
		executor.setPoolDefinitions(definitions);
		
		long t1 = System.currentTimeMillis();
		System.out.println("begin=" + t1);
		
		int total = 10000000;
		AtomicInteger count = new AtomicInteger();
		for(int i=0; i< total; i++) {
			executor.onExecute("/admin", new Runnable() {
				@Override
				public void run() {
					count.incrementAndGet();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		// 等待消费完
		CountDownLatch latch = new CountDownLatch(1);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(count.get() < total) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {}
				}
				latch.countDown();
			}
		}).start();
		latch.await();
		
		System.out.println("over ,use=" + (System.currentTimeMillis() - t1));
		System.out.println("count=" + count.get());
	}
}
