package com.swak.rpc.registry.redis;

import com.swak.cache.SafeEncoder;
import com.swak.serializer.SerializationUtils;

import io.lettuce.core.pubsub.RedisPubSubListener;

public abstract class PubSubListenerAdapter<NotifyEvent> implements RedisPubSubListener<byte[], byte[]>{

	/**
	 * 订阅主题
	 * 将 EventConsumer 进行订阅设置
	 */
	public abstract void subscribe();
	
	/**
	 * 分发消息
	 * @param channel
	 * @param message
	 */
	public abstract void onMessage(String channel, NotifyEvent message);
	
	@Override
	@SuppressWarnings("unchecked")
	public void message(byte[] channel, byte[] message) {
		this.onMessage(SafeEncoder.encode(channel), (NotifyEvent)SerializationUtils.deserialize(message));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void message(byte[] pattern, byte[] channel, byte[] message) {
		this.onMessage(SafeEncoder.encode(channel), (NotifyEvent)SerializationUtils.deserialize(message));
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