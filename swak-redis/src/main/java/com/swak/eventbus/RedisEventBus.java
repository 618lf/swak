package com.swak.eventbus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.RedisUtils;
import com.swak.eventbus.EventConsumer;
import com.swak.utils.Lists;

import io.lettuce.core.pubsub.RedisPubSubListener;

/**
 * 基于 Redis 的 event bus
 * @author lifeng
 */
public class RedisEventBus implements RedisPubSubListener<byte[], byte[]>, EventBus {
	
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
	public void onMessage(String channel, byte[] message) {
		consumers.get(channel).stream().forEach(consumer -> consumer.onMessge(message));
	}
	
	/**
	 * 订阅消息
	 * @param channels
	 */
	public RedisEventBus subscribe(String ... channels) {
		RedisUtils.subscribe(channels);
		return this;
	}
	
	@Override
	public void message(byte[] channel, byte[] message) {
		this.onMessage(SafeEncoder.encode(channel), message);
	}

	@Override
	public void message(byte[] pattern, byte[] channel, byte[] message) {
		this.onMessage(SafeEncoder.encode(channel), message);
	}

	@Override
	public void subscribed(byte[] channel, long count) {
		
	}

	@Override
	public void psubscribed(byte[] pattern, long count) {
		
	}

	@Override
	public void unsubscribed(byte[] channel, long count) {
		
	}

	@Override
	public void punsubscribed(byte[] pattern, long count) {
		
	}
}