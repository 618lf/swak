package com.swak.http.pool;

import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Stream;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.swak.common.utils.Maps;
import com.swak.common.utils.StringUtils;
import com.swak.http.Executeable;

/**
 * 用RingBuffer 来执行任务
 * 感觉性能大不如 ConfigableThreadPoolFactory
 * 估计代码上有些问题
 * @author lifeng
 */
public class RingBufferExecutor implements Executeable {

	protected final String DEFAULT_POOL_NAME = "DEFAULT";
	protected final int DEFAULT_RING_BUFFER_SIZE = 1024 * 64;
	protected final int DEFAULT_THREADSIZE = 1024;
	private Map<String, Disruptor<ConcurrentEvent>> disruptors;
	private Disruptor<ConcurrentEvent> defaultDisruptor;

	private Disruptor<ConcurrentEvent> getDisruptor(String lookupPath) {
		if (disruptors != null && !disruptors.isEmpty()) {
			for (String s : disruptors.keySet()) {
				if (StringUtils.startsWithIgnoreCase(lookupPath, s)) {
					return disruptors.get(s);
				}
			}
		}
		return this.getDefaultDisruptor();
	}

	private Disruptor<ConcurrentEvent> getDefaultDisruptor() {
		if (defaultDisruptor == null) {
			synchronized (this) {
				if (defaultDisruptor == null) {
					defaultDisruptor = this.createDisruptor(DEFAULT_THREADSIZE);
				}
			}
		}
		return defaultDisruptor;
	}

	@Override
	public void onExecute(String lookupPath, Runnable run) {
		RingBuffer<ConcurrentEvent> ringBuffer = getDisruptor(lookupPath).getRingBuffer();
		long next = ringBuffer.next();
		try {
			ConcurrentEvent commandEvent = ringBuffer.get(next);
			commandEvent.set(run);
		} finally {
			ringBuffer.publish(next);
		}
	}
	
	public void createPool(String poolName, String configs) {
		String[] _configs = configs.split(":");
		int poolSize = getDefault(_configs, 0, DEFAULT_THREADSIZE);
		
		// 如果配置了默认的线程池 -- 处理所有的其他业务,包括 not found
		if (DEFAULT_POOL_NAME.equalsIgnoreCase(poolName)) {
			defaultDisruptor = this.createDisruptor(poolSize);
			return;
		}
		
		// 其他的是业务的线程池
		if (disruptors == null) {
			disruptors = Maps.newHashMap();
		}
		disruptors.put(poolName, this.createDisruptor(poolSize));
	}
	
	protected Disruptor<ConcurrentEvent> createDisruptor(int poolSize) {
		Disruptor<ConcurrentEvent> disruptor = new Disruptor<>(new ConcurrentEventFactory(), DEFAULT_RING_BUFFER_SIZE,
				new ConcurrentThreadFactory(), ProducerType.SINGLE, new BlockingWaitStrategy());
		ConcurrentHandler worker = new ConcurrentHandler();
		ConcurrentHandler[] workers = new ConcurrentHandler[poolSize];
		Stream.iterate(0, i -> i+1).limit(poolSize).forEach(i -> {
			workers[i] = worker;
		});
		disruptor.handleEventsWithWorkerPool(workers);
		disruptor.start();
		return disruptor;
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
		private Runnable run;

		public void set(Runnable run) {
			this.run = run;
		}

		public Runnable get() {
			return this.run;
		}
		
		public void release() {
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
				Runnable executor = event.get();
				if (null != executor) {
					executor.run();
				}
			} finally {
				event.release();
			}
		}
	}
}