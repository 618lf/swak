package com.swak.eventbus;

import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.RedisUtils;

import io.lettuce.core.pubsub.RedisPubSubListener;

/**
 * 基于 Redis 的 event bus
 * @author lifeng
 */
public class RedisEventBus implements RedisPubSubListener<byte[], byte[]>{
	
	/**
	 * 订阅消息
	 * @param channels
	 */
	public RedisEventBus subscribe(String ... channels) {
		RedisUtils.subscribe(channels);
		return this;
	}
	
	/**
	 * 消费消息
	 * @param channel
	 * @param message
	 */
	protected void onMessage(String channel, byte[] message) {}

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