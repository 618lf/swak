package com.tmt.executor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.http.Executeable;
import com.swak.http.pool.RingBufferPoolExecutor;

/**
 * 100000000 次 需要 30秒
 * 单每个cpu 都是100%。
 * 所以不能使用全部的cpu才处理
 * @author lifeng
 */
public class TestRingbufferPoolExecutor {

	public static void main(String[] args) throws InterruptedException {
		String definitions = "DEFAULT = 2000:1024:5";
		Executeable executor = new RingBufferPoolExecutor();
		executor.setPoolDefinitions(definitions);
		
		long t1 = System.currentTimeMillis();
		System.out.println("begin=" + t1);
		
		int total = 100000000;
		AtomicInteger count = new AtomicInteger();
		for(int i=0; i< total; i++) {
			executor.onExecute("/admin", new Runnable() {
				@Override
				public void run() {
					count.incrementAndGet();
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
