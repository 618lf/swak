package com.tmt.queue;

import java.util.concurrent.Executors;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class EventWorker {

	private Disruptor<Event> disruptor;
	private RingBuffer<Event> ringBuffer;
	private int ringBufferSize;
	private int threadSize;

	@SuppressWarnings("deprecation")
	public void init() {
		disruptor = new Disruptor<Event>(new WorkerEventFactory(), ringBufferSize, Executors.newFixedThreadPool(threadSize),
				ProducerType.SINGLE, new BlockingWaitStrategy());
		
		ringBuffer = disruptor.getRingBuffer();
		
		// 处理异常
		disruptor.setDefaultExceptionHandler(new ExceptionHandler<Event>() {

			@Override
			public void handleEventException(Throwable ex, long sequence, Event event) {
				
			}

			@Override
			public void handleOnStartException(Throwable ex) {
				
			}

			@Override
			public void handleOnShutdownException(Throwable ex) {
				
			}
		});
		
		// 创建工作者处理器
	}
}
