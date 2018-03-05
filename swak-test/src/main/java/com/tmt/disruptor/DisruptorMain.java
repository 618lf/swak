package com.tmt.disruptor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Disruptor 的性能是 BlockingQueue 的 5 倍左右
 * @author lifeng
 */
public class DisruptorMain {

	public static void main(String[] args) throws InterruptedException {
		
		// 消费的数量
		AtomicInteger count = new AtomicInteger();
		
		// 初始化Disruptor
		Disruptor<MsgData> disruptor = new Disruptor<>(new MsgDataFactory(), 1024, MsgDataThreadFactory.INSTANCE, ProducerType.SINGLE,
				new SleepingWaitStrategy());
		
		// 启动多个消费线程
		// disruptor.handleEventsWithWorkerPool(new MsgDataHandler(count), new MsgDataHandler(count), new MsgDataHandler(count));
		disruptor.handleEventsWith(new MsgDataHandler2(count));
		disruptor.start();
		
		// 生产的总数 
		int total = 10000000;
		
		// 获取ringBuffer
		RingBuffer<MsgData> ringBuffer = disruptor.getRingBuffer();
		long t1 = System.currentTimeMillis();
		System.out.println("begin=" + t1);
		// 启动生产者
		MsgDataProducer producer = new MsgDataProducer(ringBuffer);
		for (int i = 0; i < total; i++) {
			// 模拟生成数据
			producer.pushData(i);
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
