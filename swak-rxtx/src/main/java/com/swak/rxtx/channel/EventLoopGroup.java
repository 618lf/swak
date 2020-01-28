package com.swak.rxtx.channel;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.utils.Lists;

/**
 * 线程池管理器
 * 
 * @author lifeng
 */
public class EventLoopGroup {

	private final AtomicInteger idx = new AtomicInteger();
	List<EventLoop> eventLoops;

	/**
	 * 管理器 EventLoops
	 * 
	 * @param works
	 */
	public EventLoopGroup(int works) {
		eventLoops = Lists.newArrayList(works);
		for (int i = 0; i < works; i++) {
			eventLoops.add(new EventLoop().setParent(this));
		}
	}

	/**
	 * 选择一个 EventLoop
	 * 
	 * @return
	 */
	public EventLoop next() {
		return eventLoops.get(idx.getAndIncrement() & eventLoops.size() - 1);
	}

	/**
	 * 关闭
	 */
	public void shutdown() {
		eventLoops.forEach(eventLoop -> {
			eventLoop.shutdownNow();
		});
	}
}