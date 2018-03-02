package com.tmt.disruptor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadForConsumer extends Thread {

	private BlockingQueue<String> blockingQueue;
	private ExecutorService executor;

	public ThreadForConsumer(BlockingQueue<String> blockingQueue) {
		this.blockingQueue = blockingQueue;
		this.executor = Executors.newFixedThreadPool(1024);
	}

	@Override
	public void run() {
		try {
			while (true) {
				blockingQueue.take();
				executor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}