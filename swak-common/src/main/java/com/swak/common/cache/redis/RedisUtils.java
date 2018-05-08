package com.swak.common.cache.redis;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.swak.common.cache.redis.factory.RedisConnectionFactory;
import com.swak.common.utils.SpringContextHolder;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

/**
 * Redis 的简单使用 如果全局使用，有两种方案（使用前缀，list 等集合）:先使用前缀的方式 存储字节或字符，请使用一种
 * 
 * @author lifeng
 */
public class RedisUtils {

	/**
	 * 工厂
	 */
	@SuppressWarnings("unchecked")
	private static RedisConnectionFactory<String, byte[]> factory = SpringContextHolder
			.getBean(RedisConnectionFactory.class);

	/**
	 * 获得标准的链接
	 * 
	 * @return
	 */
	public static StatefulRedisConnection<String, byte[]> getStandardConnection() {
		return factory.getStandardConnection();
	}

	/**
	 * 获得发布订阅的链接
	 * 
	 * @return
	 */
	public static StatefulRedisPubSubConnection<String, byte[]> getPubsubConnection() {
		return factory.getPubsubConnection();
	}
	
	// --------------- 基本操作相关--------
	public static <T> T execute(Function<StatefulRedisConnection<String, byte[]>, T> fuc) {
		StatefulRedisConnection<String, byte[]> connect = RedisUtils.getStandardConnection();
		try {
            return fuc.apply(connect);
		} finally {
			connect.close();
		}
	}

	/**
	 * ttl 
	 * @param key
	 * @return
	 */
	public static long ttl(String key) {
		return execute(connect -> connect.sync().ttl(key));
	}
	
	/**
	 * get 
	 * @param key
	 * @return
	 */
	public static byte[] get(String key) {
		return execute(connect -> connect.sync().get(key));
	}
	
	/**
	 * set 
	 * @param key
	 * @return
	 */
	public static void set(String key, byte[] value) {
		execute(connect -> connect.sync().set(key, value));
	}
	
	/**
	 * set 
	 * @param key
	 * @return
	 */
	public static void set(String key, byte[] value, int expire) {
		execute(connect -> {
			connect.sync().set(key, value);
			connect.sync().expire(key, expire);
			return null;
		});
	}
	
	/**
	 * del 
	 * @param key
	 * @return
	 */
	public static long del(String ... keys) {
		return execute(connect -> connect.sync().del(keys));
	}
	
	/**
	 * del 
	 * @param key
	 * @return
	 */
	public static boolean expire(String key, int expire) {
		return execute(connect -> connect.sync().expire(key, expire));
	}
	
	/**
	 * del 
	 * @param key
	 * @return
	 */
	public static boolean exists(String ... key) {
		return execute(connect -> connect.sync().exists(key) > 0);
	}
	
	/**
	 * lPush
	 * @param key
	 * @param value
	 * @return
	 */
	public static long lPush(String key, byte[] ... value) {
		return execute(connect -> connect.sync().lpush(key, value));
	}
	
	/**
	 * lPop
	 * @param key
	 * @param value
	 * @return
	 */
	public static byte[] lPop(String key) {
		return execute(connect -> connect.sync().lpop(key));
	}
	
	/**
	 * lPop
	 * @param key
	 * @param value
	 * @return
	 */
	public static long lLen(String key) {
		return execute(connect -> connect.sync().llen(key));
	}
	
	/**
	 * hget
	 * @param key
	 * @param field
	 * @return
	 */
	public static byte[] hGet(String key, String field) {
		return execute(connect -> connect.sync().hget(key, field));
	}
	
	/**
	 * hset
	 * @param key
	 * @param field
	 * @return
	 */
	public static boolean hSet(String key, String field, byte[] value) {
		return execute(connect -> connect.sync().hset(key, field, value));
	}
	
	/**
	 * hdel
	 * @param key
	 * @param field
	 * @return
	 */
	public static long hDel(String key, String ... fields) {
		return execute(connect -> connect.sync().hdel(key, fields));
	}
	
	/**
	 * hGetAll
	 * @param key
	 * @return
	 */
	public static Map<String, byte[]> hGetAll(String key) {
		return execute(connect -> connect.sync().hgetall(key));
	}
	
	/**
	 * hMset
	 * @param key
	 * @param map
	 * @return
	 */
	public static String hMSet(String key, Map<String, byte[]> map) {
		return execute(connect -> connect.sync().hmset(key, map));
	}
	
	/**
	 * runScript
	 * @param key
	 * @return
	 */
	public static <T> T runScript(String script, String[] keys, byte[][] values) {
		return execute(connect -> connect.sync().eval(script, ScriptOutputType.VALUE, keys, values));
	}
	
	// --------------- 发布订阅--------
	public static void observable(Consumer<StatefulRedisPubSubConnection<String, byte[]>> fuc) {
		StatefulRedisPubSubConnection<String, byte[]> connect = RedisUtils.getPubsubConnection();
		try {
            fuc.accept(connect);
		} finally {
			connect.close();
		}
	}
	
	/**
	 * 订阅此主题
	 * @param key
	 * @return
	 */
	public static void subscribe(RedisPubSubListener<String, byte[]> subscriber, String ... channels) {
		observable(connect -> {
			connect.addListener(subscriber);
			connect.async().subscribe(channels);
		});
	}
	
	/**
	 * 发布事件
	 * @param key
	 * @return
	 */
	public static void publish(String channel, byte[] message) {
		observable(connect -> connect.async().publish(channel, message));
	}
}