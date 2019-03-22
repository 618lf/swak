package com.swak.pubsub;

import java.util.concurrent.CompletionStage;

import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.RedisUtils;
import com.swak.serializer.SerializationUtils;

import io.lettuce.core.pubsub.RedisPubSubListener;

/**
 * 基本的消息订阅
 * 
 * @author lifeng
 */
public abstract class RedisPubSubHandler implements RedisPubSubListener<byte[], byte[]> {

	/**
	 * 订阅消息
	 * 
	 * @param channels
	 */
	protected void subscribe(String... channels) {
		RedisUtils.subscribe(channels);
	}

	/**
	 * 发布消息
	 */
	public CompletionStage<Long> publish(String channel, byte[] message) {
		return RedisUtils.publish(channel, message);
	}

	/**
	 * 发布消息
	 */
	public CompletionStage<Long> publish(String channel, Object obj) {
		return this.publish(channel, SerializationUtils.serialize(obj));
	}
	
	/**
	 * 处理消息
	 * 
	 * @param channel
	 * @param message
	 */
	public abstract void onMessage(String channel, byte[] message);

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