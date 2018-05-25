package com.swak.eventbus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.swak.utils.Lists;

/**
 * 简单的实现 event bus
 * @author lifeng
 */
public class EventBus extends RedisEventBus {

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
		return this;
	}
	
	/**
	 * 订阅主题
	 */
	public void subscribe() {
		consumers.keySet().forEach(channel -> this.subscribe(channel));
	}
	
	/**
	 * 处理消息
	 */
	@Override
	protected void onMessage(String channel, byte[] message) {
		Event event = new Event(message);
		consumers.get(channel).stream().forEach(consumer -> consumer.onMessge(event));
	}
}