package com.swak.http.pool;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.swak.common.utils.Maps;
import com.swak.http.PathMatcherHelper;

/**
 * 用RingBuffer 来执行任务
 * 感觉性能大不如 ConfigableThreadPoolFactory
 * 估计代码上有些问题
 * @author lifeng
 */
public class RingBufferConfigableThreadPoolFactory extends ConfigableThreadPoolFactory {

	private static final int DEFAULT_RING_BUFFER_SIZE = 8 * 1024;
	private Map<String, Disruptor<ConcurrentEvent>> disruptors;
	private Disruptor<ConcurrentEvent> defaultDisruptor;

	/**
	 * 创建线程池
	 */
	@Override
	public void createPool(String poolName, ThreadPoolExecutor pool) {
		// 如果配置了默认的线程池 -- 处理所有的其他业务,包括 not found
		if (default_pool_name.equalsIgnoreCase(poolName)) {
			defaultDisruptor = this.createDisruptor(pool);
			return;
		}
		
		// 其他的是业务的线程池
		if (disruptors == null) {
			disruptors = Maps.newHashMap();
		}
		disruptors.put(poolName, this.createDisruptor(pool));
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	protected Disruptor<ConcurrentEvent> createDisruptor(ThreadPoolExecutor pool) {
		Disruptor<ConcurrentEvent> disruptor = new Disruptor<>(new ConcurrentEventFactory(), DEFAULT_RING_BUFFER_SIZE,
				pool, ProducerType.SINGLE, new BlockingWaitStrategy());
		disruptor.handleEventsWith(new ConcurrentHandler());
		disruptor.start();
		return disruptor;
	}

	private Disruptor<ConcurrentEvent> getDisruptor(String lookupPath) {
		if (disruptors != null && !disruptors.isEmpty()) {
			for (String s : disruptors.keySet()) {
				if (PathMatcherHelper.getMatcher().match(s, lookupPath)) {
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
					defaultDisruptor = this.createDisruptor(this.getDefaultPool());
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

	public class ConcurrentHandler implements EventHandler<ConcurrentEvent> {

		@Override
		public void onEvent(ConcurrentEvent event, long sequence, boolean endOfBatch) throws Exception {
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