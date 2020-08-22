package com.swak.redis.lettuce;

import com.swak.SafeEncoder;
import com.swak.redis.RedisPubSubFutrue;

import io.lettuce.core.pubsub.RedisPubSubListener;

/**
 * 基于的 Lettuce 的订阅处理器
 * 
 * @author lifeng
 * @date 2020年8月20日 下午9:00:51
 */
public class LettuceRedisPubSubFuture extends RedisPubSubFutrue implements RedisPubSubListener<byte[], byte[]> {

	public LettuceRedisPubSubFuture(String channel) {
		super(channel);
	}

	@Override
	public void message(byte[] channel, byte[] message) {
		this.tryMessage(SafeEncoder.encode(channel), message);
	}

	@Override
	public void message(byte[] pattern, byte[] channel, byte[] message) {
		this.tryMessage(SafeEncoder.encode(channel), message);
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
