package com.swak.cache.redis;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.factory.RedisConnectionFactory;

import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

/**
 * Redis 的简单使用 如果全局使用，有两种方案（使用前缀，list 等集合）:先使用前缀的方式 存储字节或字符，请使用一种
 * 
 * @author lifeng
 */
public class RedisUtils {

	private static RedisConnectionFactory<byte[], byte[]> factory = null;
	public static void setRedisConnectionFactory(RedisConnectionFactory<byte[], byte[]> factory) {
		RedisUtils.factory = factory;
	}
	
	// ---------------- 同步 ---------
	public static <T> T sync(Function<RedisCommands<byte[], byte[]>, T> fuc) {
		return fuc.apply(factory.getStandardConnection().sync());
	}
	// ---------------- 异步 ------------
	public static <T> T async(Function<RedisAsyncCommands<byte[], byte[]>, T> fuc) {
		return fuc.apply(factory.getStandardConnection().async());
	}
	// ---------------- 响应式 ------------
	public static <T> T reactive(Function<RedisReactiveCommands<byte[], byte[]>, T> fuc) {
		return fuc.apply(factory.getStandardConnection().reactive());
	}

	// --------------- 发布订阅 (需要是不同的链接) --------
	public static <T> T subscribe(Function<StatefulRedisPubSubConnection<byte[], byte[]>, T> fuc) {
		return fuc.apply(factory.getSubscribeConnection());
	}
	public static <T> T publish(Function<StatefulRedisPubSubConnection<byte[], byte[]>, T> fuc) {
		return fuc.apply(factory.getPublishConnection());
	}
	
	/**
	 * 添加监听
	 * @param key
	 * @return
	 */
	public static CompletionStage<Void> listener(RedisPubSubListener<byte[], byte[]> subscriber, String ... channels) {
		return subscribe(connect -> {
			connect.addListener(subscriber);
			return connect.async().subscribe(SafeEncoder.encodeMany(channels));
		});
	}
	
	/**
	 * 发布事件
	 * @param key
	 * @return
	 */
	public static CompletionStage<Long> publish(String channel, byte[] message) {
		return publish(connect -> connect.async().publish(SafeEncoder.encode(channel), message));
	}
}