package com.swak.http.pool;

import java.util.concurrent.ThreadFactory;
import java.util.stream.Stream;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 只是在执行这个任务的时候先放入ringbuffer。
 * 然后通过ringbufer 的worker来分发任务。
 * 测试：
 * 和 netty 配合使用，感觉性能不好，没有直接使用线程池的性能高。
 * 是否只能作为单机队列来使用。
 * @see TestRingbufferPoolExecutor
 * @author lifeng
 */
public class RingBufferPoolExecutor extends ConfigableThreadPoolExecutor {
	
	protected final int DEFAULT_RING_BUFFER_SIZE = 1024 * 64;
	private RingBuffer<ConcurrentEvent> ringBuffer;
	
	public RingBufferPoolExecutor() {
		Disruptor<ConcurrentEvent> disruptor = new Disruptor<>(new ConcurrentEventFactory(), DEFAULT_RING_BUFFER_SIZE,
				new ConcurrentThreadFactory(), ProducerType.SINGLE, new YieldingWaitStrategy());
		int poolSize = Runtime.getRuntime().availableProcessors() / 2;
		ConcurrentHandler worker = new ConcurrentHandler();
		ConcurrentHandler[] workers = new ConcurrentHandler[poolSize];
		Stream.iterate(0, i -> i+1).limit(poolSize).forEach(i -> {
			workers[i] = worker;
		});
		disruptor.handleEventsWithWorkerPool(workers);
		disruptor.start();
		
		// 得到缓冲区
		ringBuffer = disruptor.getRingBuffer();
	}

	/**
	 * 先放入 ringbuffer， 然后在执行任务
	 */
	@Override
	public void onExecute(String lookupPath, Runnable run) {
		long next = ringBuffer.next();
		try {
			ConcurrentEvent commandEvent = ringBuffer.get(next);
			commandEvent.init(lookupPath, run);
		} finally {
			ringBuffer.publish(next);
		}
	}
	
	/**
	 * 用默认的线程池执行
	 * @param lookupPath
	 * @param run
	 */
	private void _onExecute(String lookupPath, Runnable run) {
		super.onExecute(lookupPath, run);
	}
	
	public class ConcurrentThreadFactory implements ThreadFactory {
		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setDaemon(true);
			thread.setName("Disruptor-worker");
			return thread;
		}
	}

	public class ConcurrentEvent {
		
		private String path;
		private Runnable run;

		public void init(String path, Runnable run) {
			this.path = path;
			this.run = run;
		}
		public Runnable get() {
			return this.run;
		}
		public String getPath() {
			return path;
		}
		public void release() {
			this.path = null;
			this.run = null;
		}
	}

	public class ConcurrentEventFactory implements EventFactory<ConcurrentEvent> {
		@Override
		public ConcurrentEvent newInstance() {
			return new ConcurrentEvent();
		}
	}

	public class ConcurrentHandler implements WorkHandler<ConcurrentEvent> {

		@Override
		public void onEvent(ConcurrentEvent event) throws Exception {
			try {
				String path = event.getPath();
				Runnable run = event.get();
				if (null != run && path != null) {
					
					// 用线程池来执行
					_onExecute(path, run);
				}
			} finally {
				event.release();
			}
		}
	}
}