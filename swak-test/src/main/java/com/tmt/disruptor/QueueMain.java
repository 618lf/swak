package com.tmt.disruptor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueMain {

	public static void main(String[] args) throws InterruptedException {
		
		// 消费的数量
		AtomicInteger count = new AtomicInteger();
				
		// TODO Auto-generated method stub
		// 初始化阻塞队列
		BlockingQueue<MsgData> blockingQueue = new ArrayBlockingQueue<>(1024);

		// 创建消费者线程
		int total = 10000000;
				
		Thread consumer = new ThreadForConsumer(count, blockingQueue);
		consumer.setDaemon(true); consumer.start();
		long t1 = System.currentTimeMillis();
		System.out.println("begin=" + t1);
		// 创建数据
		for (int i = 0; i < total; i++) {
			blockingQueue.put(new MsgData());
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
	
	public static class ThreadForConsumer extends Thread {

		private BlockingQueue<MsgData> blockingQueue;
		private AtomicInteger count;

		public ThreadForConsumer(AtomicInteger count, BlockingQueue<MsgData> blockingQueue) {
			this.blockingQueue = blockingQueue;
			this.count = count;
		}

		@Override
		public void run() {
			try {
				while (true) {
					blockingQueue.take();
					count.incrementAndGet();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
