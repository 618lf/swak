package com.swak.queue;

import java.util.concurrent.ArrayBlockingQueue;

import com.swak.utils.Queues;

public class QueueTest {

	public static void main(String[] args) {
		ArrayBlockingQueue<String> queue = Queues.newArrayBlockingQueue(2);
		queue.add("1");
		queue.add("2");
		queue.add("3");
		System.out.println(queue.size());
	}
}
