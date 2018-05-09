package com.swak.common.eventbus.impl;

import com.swak.common.cache.redis.RedisUtils;
import com.swak.common.eventbus.EventProducer;
import com.swak.common.serializer.SerializationUtils;

/**
 * 基于 reids 的事件发送
 * @author lifeng
 */
public class RedisEventProducer implements EventProducer {

	/**
	 * 发布
	 */
	@Override
	public void publish(String channel, byte[] message) {
		RedisUtils.publish(channel, message);
	}

	/**
	 * 发布
	 */
	@Override
	public void publish(String channel, Object obj) {
		this.publish(channel, SerializationUtils.serialize(obj));
	}
}