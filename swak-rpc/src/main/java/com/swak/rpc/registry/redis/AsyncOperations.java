package com.swak.rpc.registry.redis;

import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.factory.RedisConnectionFactory;

import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

/**
 * 提供一组一步操作Api
 * @author lifeng
 */
public class AsyncOperations {
	
	private RedisConnectionFactory<byte[], byte[]> factory;
	public AsyncOperations(RedisConnectionFactory<byte[], byte[]> factory) {
		this.factory = factory;
	}
	
	// ---------------- 异步 ------------
	public <T> T async(Function<RedisAsyncCommands<byte[], byte[]>, T> fuc) {
		return fuc.apply(factory.getStandardConnection().async());
	}
	// --------------- 发布订阅 (需要是不同的链接) --------
	public <T> T subscribe(Function<StatefulRedisPubSubConnection<byte[], byte[]>, T> fuc) {
		return fuc.apply(factory.getSubscribeConnection());
	}
	public <T> T publish(Function<StatefulRedisPubSubConnection<byte[], byte[]>, T> fuc) {
		return fuc.apply(factory.getPublishConnection());
	}
	
	/**
	 * hset
	 * @param key
	 * @param field
	 * @return
	 */
	public CompletionStage<Boolean> hSet(String key, String field, byte[] value) {
		return async(connect -> connect.hset(SafeEncoder.encode(key), SafeEncoder.encode(field), value));
	}
	
	/**
	 * hdel
	 * @param key
	 * @param field
	 * @return
	 */
	public CompletionStage<Long> hDel(String key, String ... fields) {
		return async(connect -> connect.hdel(SafeEncoder.encode(key), SafeEncoder.encodeMany(fields)));
	}
	
	/**
	 * hGetAll
	 * @param key
	 * @return
	 */
	public CompletionStage<Map<byte[], byte[]>> hGetAll(String key) {
		return async(connect -> connect.hgetall(SafeEncoder.encode(key))) ;
	}
	
	/**
	 * 添加监听
	 * @param key
	 * @return
	 */
	public boolean listener(RedisPubSubListener<byte[], byte[]> subscriber) {
		return subscribe(connect -> {
			connect.addListener(subscriber);
			return true;
		});
	}
	
	/**
	 * 订阅此主题
	 * @param key
	 * @return
	 */
	public CompletionStage<Void> subscribe(String ... channels) {
		return subscribe(connect -> {
			return connect.async().subscribe(SafeEncoder.encodeMany(channels));
		});
	}
	
	/**
	 * 发布事件
	 * @param key
	 * @return
	 */
	public CompletionStage<Long> publish(String channel, String message) {
		return publish(connect -> connect.async().publish(SafeEncoder.encode(channel), SafeEncoder.encode(message)));
	}
}
