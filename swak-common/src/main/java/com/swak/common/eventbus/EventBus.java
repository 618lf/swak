package com.swak.common.eventbus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.swak.common.utils.Lists;

/**
 * 简单的实现 event bus
 * @author lifeng
 */
public class EventBus extends RedisEventBus{

	private Map<String, List<EventConsumer>> consumers = new ConcurrentHashMap<>(2);
	
	/**
	 * 添加订阅
	 * @param consumer
	 * @return
	 */
	public EventBus addConsumer(EventConsumer consumer) {
		consumers.computeIfAbsent(consumer.getChannel(), (channel) ->{
			return Lists.newArrayList(3);
		}).add(consumer);
		this.subscribe(consumer.getChannel());
		return this;
	}
	
	@Override
	protected void onMessage(String channel, byte[] message) {
		Event event = new Event(message);
		consumers.get(channel).stream().forEach(consumer -> consumer.onMessge(event));
	}
}