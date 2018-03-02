package com.tmt.disruptor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class QueueMain {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		// 初始化阻塞队列
		BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1024);

		// 创建消费者线程

		Thread consumer = new Thread(new ThreadForConsumer(blockingQueue));
		consumer.start();
		long t1 = System.currentTimeMillis();
		System.out.println("begin=" + t1);
		// 创建数据
		for (int i = 0; i <= 10000; i++) {
			blockingQueue.put(i + "");
		}
		System.out.println("over ,user=" + (System.currentTimeMillis() - t1));

	}

}
