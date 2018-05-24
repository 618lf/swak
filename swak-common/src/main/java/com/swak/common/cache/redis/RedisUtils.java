package com.swak.common.cache.redis;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.swak.common.cache.SafeEncoder;
import com.swak.common.cache.redis.factory.RedisConnectionFactory;

import io.lettuce.core.ScriptOutputType;
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

	/**
	 * ttl 
	 * @param key
	 * @return
	 */
	public static long ttl(String key) {
		return sync(connect -> connect.ttl(SafeEncoder.encode(key)));
	}
	
	/**
	 * get 
	 * @param key
	 * @return
	 */
	public static byte[] get(String key) {
		return sync(connect -> connect.get(SafeEncoder.encode(key)));
	}
	
	/**
	 * set 
	 * @param key
	 * @return
	 */
	public static void set(String key, byte[] value) {
		sync(connect -> connect.set(SafeEncoder.encode(key), value));
	}
	
	/**
	 * set 
	 * @param key
	 * @return
	 */
	public static void set(String key, byte[] value, int expire) {
		sync(connect -> {
			byte[] _key = SafeEncoder.encode(key);
			connect.set(_key, value);
			connect.expire(_key, expire);
			return null;
		});
	}
	
	/**
	 * del 
	 * @param key
	 * @return
	 */
	public static long del(String ... keys) {
		return sync(connect -> connect.del(SafeEncoder.encodeMany(keys)));
	}
	
	/**
	 * del 
	 * @param key
	 * @return
	 */
	public static boolean expire(String key, int expire) {
		return sync(connect -> connect.expire(SafeEncoder.encode(key), expire));
	}
	
	/**
	 * del 
	 * @param key
	 * @return
	 */
	public static boolean exists(String ... key) {
		return sync(connect -> connect.exists(SafeEncoder.encodeMany(key)) > 0);
	}
	
	/**
	 * lPush
	 * @param key
	 * @param value
	 * @return
	 */
	public static long lPush(String key, byte[] ... value) {
		return sync(connect -> connect.lpush(SafeEncoder.encode(key), value));
	}
	
	/**
	 * lPop
	 * @param key
	 * @param value
	 * @return
	 */
	public static byte[] lPop(String key) {
		return sync(connect -> connect.lpop(SafeEncoder.encode(key)));
	}
	
	/**
	 * lPop
	 * @param key
	 * @param value
	 * @return
	 */
	public static long lLen(String key) {
		return sync(connect -> connect.llen(SafeEncoder.encode(key)));
	}
	
	/**
	 * hget
	 * @param key
	 * @param field
	 * @return
	 */
	public static byte[] hGet(String key, String field) {
		return sync(connect -> connect.hget(SafeEncoder.encode(key), SafeEncoder.encode(field)));
	}
	
	/**
	 * hset
	 * @param key
	 * @param field
	 * @return
	 */
	public static boolean hSet(String key, String field, byte[] value) {
		return sync(connect -> connect.hset(SafeEncoder.encode(key), SafeEncoder.encode(field), value));
	}
	
	/**
	 * hdel
	 * @param key
	 * @param field
	 * @return
	 */
	public static long hDel(String key, String ... fields) {
		return sync(connect -> connect.hdel(SafeEncoder.encode(key), SafeEncoder.encodeMany(fields)));
	}
	
	/**
	 * hGetAll
	 * @param key
	 * @return
	 */
	public static Map<byte[], byte[]> hGetAll(String key) {
		return sync(connect -> connect.hgetall(SafeEncoder.encode(key))) ;
	}
	
	/**
	 * hMset
	 * @param key
	 * @param map
	 * @return
	 */
	public static String hMSet(String key, Map<byte[], byte[]> map) {
		return sync(connect -> connect.hmset(SafeEncoder.encode(key), map));
	}
	
	/**
	 * runScript
	 * @param key
	 * @return
	 */
	public static <T> T runScript(String script, byte[][] values) {
		return sync(connect -> connect.eval(script, ScriptOutputType.VALUE, values, values[0]));
	}
	
	// --------------- 发布订阅 (需要是不同的链接) --------
	public static void subscribe(Consumer<StatefulRedisPubSubConnection<byte[], byte[]>> fuc) {
		fuc.accept(factory.getSubscribeConnection());
	}
	public static void publish(Consumer<StatefulRedisPubSubConnection<byte[], byte[]>> fuc) {
		fuc.accept(factory.getPublishConnection());
	}
	
	/**
	 * 添加监听
	 * @param key
	 * @return
	 */
	public static void listener(RedisPubSubListener<byte[], byte[]> subscriber) {
		subscribe(connect -> {
			connect.addListener(subscriber);
		});
	}
	
	/**
	 * 订阅此主题
	 * @param key
	 * @return
	 */
	public static void subscribe(String ... channels) {
		subscribe(connect -> {
			connect.async().subscribe(SafeEncoder.encodeMany(channels));
		});
	}
	
	/**
	 * 发布事件
	 * @param key
	 * @return
	 */
	public static void publish(String channel, byte[] message) {
		publish(connect -> connect.async().publish(SafeEncoder.encode(channel), message));
	}
}