package com.swak.redis.lettuce;

import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

import com.swak.SafeEncoder;
import com.swak.redis.MessageListener;
import com.swak.redis.RedisAsyncPubSubCommands;

import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;

/**
 * Lettuce 发布订阅
 * 
 * @author lifeng
 * @date 2020年8月19日 下午8:31:39
 */
public class LettuceRedisAsyncPubSubCommands implements RedisAsyncPubSubCommands<byte[], byte[]> {

	private static Map<MessageListener, RedisPubSubAdapter<byte[], byte[]>> listeners = new ConcurrentHashMap<>();

	RedisPubSubAsyncCommands<byte[], byte[]> connect;

	LettuceRedisAsyncPubSubCommands(RedisPubSubAsyncCommands<byte[], byte[]> connect) {
		this.connect = connect;
	}

	/**
	 * 添加监听
	 * 
	 * @param listener
	 */
	@Override
	public void listen(MessageListener listener) {
		listeners.computeIfAbsent(listener, (k) -> {
			RedisPubSubAdapter<byte[], byte[]> _listener = new RedisPubSubAdapter<byte[], byte[]>() {
				@Override
				public void message(byte[] channel, byte[] message) {
					listener.onMessage(SafeEncoder.encode(channel), message);
				}

				@Override
				public void message(byte[] pattern, byte[] channel, byte[] message) {
					listener.onMessage(SafeEncoder.encode(channel), message);
				}
			};
			this.connect.getStatefulConnection().addListener(_listener);
			return _listener;
		});
	}

	/**
	 * 发布消息
	 * 
	 * @param channel
	 * @param message
	 * @return
	 */
	@Override
	public CompletionStage<Long> publish(byte[] channel, byte[] message) {
		return this.connect.publish(channel, message);
	}

	/**
	 * 订阅消息，并设置监听器
	 * 
	 * @param listener
	 * @param channel
	 * @return
	 */
	@Override
	public CompletionStage<Void> subscribe(MessageListener listener, byte[]... channel) {
		listeners.computeIfAbsent(listener, (k) -> {
			RedisPubSubAdapter<byte[], byte[]> _listener = new RedisPubSubAdapter<byte[], byte[]>() {
				@Override
				public void message(byte[] channel, byte[] message) {
					listener.onMessage(SafeEncoder.encode(channel), message);
				}

				@Override
				public void message(byte[] pattern, byte[] channel, byte[] message) {
					listener.onMessage(SafeEncoder.encode(channel), message);
				}
			};
			this.connect.getStatefulConnection().addListener(_listener);
			return _listener;
		});
		return this.connect.subscribe(channel);
	}

	/**
	 * 订阅消息，并设置监听器
	 * 
	 * @param listener
	 * @param channel
	 * @return
	 */
	@Override
	public CompletionStage<Void> subscribe(byte[]... channels) {
		return this.connect.subscribe(channels);
	}

	/**
	 * 订阅消息，并设置监听器
	 * 
	 * @param listener
	 * @param channel
	 * @return
	 */
	@Override
	public CompletionStage<Void> unSubscribe(MessageListener listener, byte[]... channels) {
		listeners.computeIfPresent(listener, (k, v) -> {
			this.connect.getStatefulConnection().removeListener(v);
			return null;
		});
		return this.connect.unsubscribe(channels);
	}

	/**
	 * 订阅消息，并设置监听器
	 * 
	 * @param listener
	 * @param channel
	 * @return
	 */
	@Override
	public CompletionStage<Void> unSubscribe(byte[]... channels) {
		return this.connect.unsubscribe(channels);
	}
}
