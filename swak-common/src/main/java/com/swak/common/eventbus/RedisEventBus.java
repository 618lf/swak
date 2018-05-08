package com.swak.common.eventbus;

import io.lettuce.core.pubsub.RedisPubSubListener;

/**
 * 基于 Redis 的 event bus
 * @author lifeng
 */
public class RedisEventBus implements RedisPubSubListener<String, byte[]>{

	@Override
	public void message(String channel, byte[] message) {
		
	}

	@Override
	public void message(String pattern, String channel, byte[] message) {
		
	}

	@Override
	public void subscribed(String channel, long count) {
		
	}

	@Override
	public void psubscribed(String pattern, long count) {
		
	}

	@Override
	public void unsubscribed(String channel, long count) {
		
	}

	@Override
	public void punsubscribed(String pattern, long count) {
		
	}
}