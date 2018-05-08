package com.swak.common.eventbus;

import io.lettuce.core.pubsub.RedisPubSubListener;

/**
 * 基于 Redis 的 event bus
 * @author lifeng
 */
public class RedisEventBus implements RedisPubSubListener<byte[], byte[]>{

	@Override
	public void message(byte[] channel, byte[] message) {
		
	}

	@Override
	public void message(byte[] pattern, byte[] channel, byte[] message) {
		
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