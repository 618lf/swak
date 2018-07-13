package com.swak.cache.redis.operations;

import java.util.Map;

import com.swak.cache.Cons;
import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.RedisUtils;

import io.lettuce.core.ScriptOutputType;

/**
 * 同步操作
 * @author lifeng
 */
public class SyncOperations {

	/**
	 * ttl 
	 * @param key
	 * @return
	 */
	public static long ttl(String key) {
		return RedisUtils.sync(connect -> connect.ttl(SafeEncoder.encode(key)));
	}
	
	/**
	 * get 
	 * @param key
	 * @return
	 */
	public static byte[] get(String key) {
		return RedisUtils.sync(connect -> connect.get(SafeEncoder.encode(key)));
	}
	
	/**
	 * set 
	 * @param key
	 * @return
	 */
	public static String set(String key, byte[] value) {
		return RedisUtils.sync(connect -> connect.set(SafeEncoder.encode(key), value));
	}
	
	/**
	 * set 
	 * @param key
	 * @return
	 */
	public static String set(String key, byte[] value, int expire) {
		String script = Cons.PUT_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(key), value, SafeEncoder.encode(String.valueOf(expire))};
		SyncOperations.runScript(script, ScriptOutputType.INTEGER, values);
		return key;
	}
	
	/**
	 * del 
	 * @param key
	 * @return
	 */
	public static long del(String ... keys) {
		return RedisUtils.sync(connect -> connect.del(SafeEncoder.encodeMany(keys)));
	}
	
	/**
	 * expire 
	 * @param key
	 * @return
	 */
	public static boolean expire(String key, int expire) {
		return RedisUtils.sync(connect -> connect.expire(SafeEncoder.encode(key), expire));
	}
	
	/**
	 * exists 
	 * @param key
	 * @return
	 */
	public static Long exists(String ... key) {
		return RedisUtils.sync(connect -> connect.exists(SafeEncoder.encodeMany(key)));
	}
	
	/**
	 * lPush
	 * @param key
	 * @param value
	 * @return
	 */
	public static long lPush(String key, byte[] ... value) {
		return RedisUtils.sync(connect -> connect.lpush(SafeEncoder.encode(key), value));
	}
	
	/**
	 * lPop
	 * @param key
	 * @param value
	 * @return
	 */
	public static byte[] lPop(String key) {
		return RedisUtils.sync(connect -> connect.lpop(SafeEncoder.encode(key)));
	}
	
	/**
	 * lPop
	 * @param key
	 * @param value
	 * @return
	 */
	public static long lLen(String key) {
		return RedisUtils.sync(connect -> connect.llen(SafeEncoder.encode(key)));
	}
	
	/**
	 * hget
	 * @param key
	 * @param field
	 * @return
	 */
	public static byte[] hGet(String key, String field) {
		return RedisUtils.sync(connect -> connect.hget(SafeEncoder.encode(key), SafeEncoder.encode(field)));
	}
	
	/**
	 * hset
	 * @param key
	 * @param field
	 * @return
	 */
	public static boolean hSet(String key, String field, byte[] value) {
		return RedisUtils.sync(connect -> connect.hset(SafeEncoder.encode(key), SafeEncoder.encode(field), value));
	}
	
	/**
	 * hdel
	 * @param key
	 * @param field
	 * @return
	 */
	public static long hDel(String key, String ... fields) {
		return RedisUtils.sync(connect -> connect.hdel(SafeEncoder.encode(key), SafeEncoder.encodeMany(fields)));
	}
	
	/**
	 * hGetAll
	 * @param key
	 * @return
	 */
	public static Map<byte[], byte[]> hGetAll(String key) {
		return RedisUtils.sync(connect -> connect.hgetall(SafeEncoder.encode(key))) ;
	}
	
	/**
	 * hMset
	 * @param key
	 * @param map
	 * @return
	 */
	public static String hMSet(String key, Map<byte[], byte[]> map) {
		return RedisUtils.sync(connect -> connect.hmset(SafeEncoder.encode(key), map));
	}
	
	/**
	 * runScript
	 * @param key
	 * @return
	 */
	public static <T> T runScript(String script, ScriptOutputType type, byte[][] values) {
		return RedisUtils.sync(connect -> connect.eval(script, type, values, values[0]));
	}
}