package com.swak.eventbus;

import java.util.concurrent.CompletionStage;

import com.swak.cache.redis.RedisUtils;
import com.swak.eventbus.EventProducer;
import com.swak.serializer.SerializationUtils;

/**
 * 基于 reids 的事件发送
 * @author lifeng
 */
public class RedisEventProducer implements EventProducer {

	/**
	 * 发布
	 */
	@Override
	public CompletionStage<Long> publish(String channel, byte[] message) {
		return RedisUtils.publish(channel, message);
	}

	/**
	 * 发布
	 */
	@Override
	public CompletionStage<Long> publish(String channel, Object obj) {
		return this.publish(channel, SerializationUtils.serialize(obj));
	}
}