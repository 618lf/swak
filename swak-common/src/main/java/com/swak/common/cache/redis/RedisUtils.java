package com.swak.common.cache.redis;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.swak.common.cache.SafeEncoder;
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
	private static RedisConnectionFactory<byte[], byte[]> factory = SpringContextHolder
			.getBean(RedisConnectionFactory.class);

	/**
	 * 获得标准的链接
	 * 
	 * @return
	 */
	public static StatefulRedisConnection<byte[], byte[]> getStandardConnection() {
		return factory.getStandardConnection();
	}

	/**
	 * 获得发布订阅的链接
	 * 
	 * @return
	 */
	public static StatefulRedisPubSubConnection<byte[], byte[]> getPubsubConnection() {
		return factory.getPubsubConnection();
	}
	
	// --------------- 基本操作相关--------
	public static <T> T execute(Function<StatefulRedisConnection<byte[], byte[]>, T> fuc) {
		return fuc.apply(RedisUtils.getStandardConnection());
	}

	/**
	 * ttl 
	 * @param key
	 * @return
	 */
	public static long ttl(String key) {
		return execute(connect -> connect.sync().ttl(SafeEncoder.encode(key)));
	}
	
	/**
	 * get 
	 * @param key
	 * @return
	 */
	public static byte[] get(String key) {
		return execute(connect -> connect.sync().get(SafeEncoder.encode(key)));
	}
	
	/**
	 * set 
	 * @param key
	 * @return
	 */
	public static void set(String key, byte[] value) {
		execute(connect -> connect.async().set(SafeEncoder.encode(key), value));
	}
	
	/**
	 * set 
	 * @param key
	 * @return
	 */
	public static void set(String key, byte[] value, int expire) {
		execute(connect -> {
			byte[] _key = SafeEncoder.encode(key);
			connect.async().set(_key, value);
			connect.async().expire(_key, expire);
			return null;
		});
	}
	
	/**
	 * del 
	 * @param key
	 * @return
	 */
	public static long del(String ... keys) {
		return execute(connect -> connect.sync().del(SafeEncoder.encodeMany(keys)));
	}
	
	/**
	 * del 
	 * @param key
	 * @return
	 */
	public static boolean expire(String key, int expire) {
		return execute(connect -> connect.sync().expire(SafeEncoder.encode(key), expire));
	}
	
	/**
	 * del 
	 * @param key
	 * @return
	 */
	public static boolean exists(String ... key) {
		return execute(connect -> connect.sync().exists(SafeEncoder.encodeMany(key)) > 0);
	}
	
	/**
	 * lPush
	 * @param key
	 * @param value
	 * @return
	 */
	public static long lPush(String key, byte[] ... value) {
		return execute(connect -> connect.sync().lpush(SafeEncoder.encode(key), value));
	}
	
	/**
	 * lPop
	 * @param key
	 * @param value
	 * @return
	 */
	public static byte[] lPop(String key) {
		return execute(connect -> connect.sync().lpop(SafeEncoder.encode(key)));
	}
	
	/**
	 * lPop
	 * @param key
	 * @param value
	 * @return
	 */
	public static long lLen(String key) {
		return execute(connect -> connect.sync().llen(SafeEncoder.encode(key)));
	}
	
	/**
	 * hget
	 * @param key
	 * @param field
	 * @return
	 */
	public static byte[] hGet(String key, String field) {
		return execute(connect -> connect.sync().hget(SafeEncoder.encode(key), SafeEncoder.encode(field)));
	}
	
	/**
	 * hset
	 * @param key
	 * @param field
	 * @return
	 */
	public static boolean hSet(String key, String field, byte[] value) {
		return execute(connect -> connect.sync().hset(SafeEncoder.encode(key), SafeEncoder.encode(field), value));
	}
	
	/**
	 * hdel
	 * @param key
	 * @param field
	 * @return
	 */
	public static long hDel(String key, String ... fields) {
		return execute(connect -> connect.sync().hdel(SafeEncoder.encode(key), SafeEncoder.encodeMany(fields)));
	}
	
	/**
	 * hGetAll
	 * @param key
	 * @return
	 */
	public static Map<byte[], byte[]> hGetAll(String key) {
		return execute(connect -> connect.sync().hgetall(SafeEncoder.encode(key))) ;
	}
	
	/**
	 * hMset
	 * @param key
	 * @param map
	 * @return
	 */
	public static String hMSet(String key, Map<byte[], byte[]> map) {
		return execute(connect -> connect.sync().hmset(SafeEncoder.encode(key), map));
	}
	
	/**
	 * runScript
	 * @param key
	 * @return
	 */
	public static <T> T runScript(String script, byte[][] values) {
		return execute(connect -> connect.sync().eval(script, ScriptOutputType.VALUE, values, values[0]));
	}
	
	// --------------- 发布订阅--------
	public static void observable(Consumer<StatefulRedisPubSubConnection<byte[], byte[]>> fuc) {
		fuc.accept(RedisUtils.getPubsubConnection());
	}
	
	/**
	 * 添加监听
	 * @param key
	 * @return
	 */
	public static void listener(RedisPubSubListener<byte[], byte[]> subscriber) {
		observable(connect -> {
			connect.addListener(subscriber);
		});
	}
	
	/**
	 * 订阅此主题
	 * @param key
	 * @return
	 */
	public static void subscribe(String ... channels) {
		observable(connect -> {
			connect.async().subscribe(SafeEncoder.encodeMany(channels));
		});
	}
	
	/**
	 * 发布事件
	 * @param key
	 * @return
	 */
	public static void publish(String channel, byte[] message) {
		observable(connect -> connect.async().publish(SafeEncoder.encode(channel), message));
	}
}