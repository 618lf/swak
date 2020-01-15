package com.swak.scheduler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SchedulerTest {

	public static void main(String[] args) throws InterruptedException {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2, new ThreadFactory() {
			private AtomicInteger count = new AtomicInteger(1);

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(true);
				t.setName("Task-scheduler-" + count.getAndIncrement());
				return t;
			}
		});
		scheduler.scheduleAtFixedRate(() -> {
			System.out.println("main:" + Thread.currentThread().getName());
			for (int i = 0; i < 4; i++) {
				scheduler.schedule(new Job(), 0, TimeUnit.SECONDS);
			}
		}, 1, 10, TimeUnit.SECONDS);

		CountDownLatch latch = new CountDownLatch(1);
		latch.await();
	}

	public static class Job implements Runnable {

		@Override
		public void run() {
			System.out.println("job:" + Thread.currentThread().getName());
		}
	}
}
