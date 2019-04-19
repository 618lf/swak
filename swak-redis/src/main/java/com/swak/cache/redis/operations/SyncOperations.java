package com.swak.cache.redis.operations;

import java.util.Map;

import com.swak.cache.Cons;
import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.RedisUtils;

import io.lettuce.core.ScriptOutputType;

/**
 * 同步操作
 * 
 * @author lifeng
 */
public class SyncOperations {

	/**
	 * ttl
	 * 
	 * @param key
	 * @return
	 */
	public static long ttl(String key) {
		return RedisUtils.sync(connect -> connect.ttl(SafeEncoder.encode(key)));
	}

	/**
	 * get
	 * 
	 * @param key
	 * @return
	 */
	public static byte[] get(String key) {
		return RedisUtils.sync(connect -> connect.get(SafeEncoder.encode(key)));
	}

	/**
	 * set
	 * 
	 * @param key
	 * @return
	 */
	public static String set(String key, byte[] value) {
		return RedisUtils.sync(connect -> connect.set(SafeEncoder.encode(key), value));
	}

	/**
	 * set
	 * 
	 * @param key
	 * @return
	 */
	public static String set(String key, byte[] value, int expire) {
		String script = Cons.PUT_LUA;
		byte[][] values = new byte[][] { SafeEncoder.encode(key), value, SafeEncoder.encode(String.valueOf(expire)) };
		SyncOperations.runScript(script, ScriptOutputType.INTEGER, values);
		return key;
	}

	/**
	 * del
	 * 
	 * @param key
	 * @return
	 */
	public static long del(String... keys) {
		return RedisUtils.sync(connect -> connect.del(SafeEncoder.encodeMany(keys)));
	}

	/**
	 * expire
	 * 
	 * @param key
	 * @return
	 */
	public static boolean expire(String key, int expire) {
		return RedisUtils.sync(connect -> connect.expire(SafeEncoder.encode(key), expire));
	}

	/**
	 * exists
	 * 
	 * @param key
	 * @return
	 */
	public static Long exists(String... key) {
		return RedisUtils.sync(connect -> connect.exists(SafeEncoder.encodeMany(key)));
	}
	
	/**
	 * incr
	 * 
	 * @param key
	 * @return
	 */
	public static Long incr(String key) {
		return RedisUtils.sync(connect -> connect.incr(SafeEncoder.encode(key)));
	}
	
	/**
	 * decr
	 * 
	 * @param key
	 * @return
	 */
	public static Long decr(String key) {
		return RedisUtils.sync(connect -> connect.decr(SafeEncoder.encode(key)));
	}

	/**
	 * lPush
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static long lPush(String key, byte[]... value) {
		return RedisUtils.sync(connect -> connect.lpush(SafeEncoder.encode(key), value));
	}
	
	/**
	 * lGet 只获取不删除
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static byte[] lGet(String key) {
		return RedisUtils.sync(connect -> connect.lindex(SafeEncoder.encode(key), 0));
	}

	/**
	 * lPop
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static byte[] lPop(String key) {
		return RedisUtils.sync(connect -> connect.lpop(SafeEncoder.encode(key)));
	}

	/**
	 * rPush
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static long rPush(String key, byte[]... value) {
		return RedisUtils.sync(connect -> connect.rpush(SafeEncoder.encode(key), value));
	}
	
	/**
	 * rGet 只获取不删除
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static byte[] rGet(String key) {
		return RedisUtils.sync(connect -> connect.lindex(SafeEncoder.encode(key), -1));
	}

	/**
	 * rPop
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static byte[] rPop(String key) {
		return RedisUtils.sync(connect -> connect.rpop(SafeEncoder.encode(key)));
	}

	/**
	 * lPop
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static long lLen(String key) {
		return RedisUtils.sync(connect -> connect.llen(SafeEncoder.encode(key)));
	}

	/**
	 * hget
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public static byte[] hGet(String key, String field) {
		return RedisUtils.sync(connect -> connect.hget(SafeEncoder.encode(key), SafeEncoder.encode(field)));
	}

	/**
	 * hset
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public static boolean hSet(String key, String field, byte[] value) {
		return RedisUtils.sync(connect -> connect.hset(SafeEncoder.encode(key), SafeEncoder.encode(field), value));
	}

	/**
	 * hdel
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public static long hDel(String key, String... fields) {
		return RedisUtils.sync(connect -> connect.hdel(SafeEncoder.encode(key), SafeEncoder.encodeMany(fields)));
	}

	/**
	 * hGetAll
	 * 
	 * @param key
	 * @return
	 */
	public static Map<byte[], byte[]> hGetAll(String key) {
		return RedisUtils.sync(connect -> connect.hgetall(SafeEncoder.encode(key)));
	}

	/**
	 * hMset
	 * 
	 * @param key
	 * @param map
	 * @return
	 */
	public static String hMSet(String key, Map<byte[], byte[]> map) {
		return RedisUtils.sync(connect -> connect.hmset(SafeEncoder.encode(key), map));
	}

	/**
	 * sAdd
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean sAdd(String key, byte[] value) {
		return RedisUtils.sync(connect -> {
			Long res = connect.sadd(SafeEncoder.encode(key), value);
			return res != null && res > 0;
		});
	}

	/**
	 * sAdd
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean sExists(String key, byte[] value) {
		return RedisUtils.sync(connect -> connect.sismember(SafeEncoder.encode(key), value));
	}

	/**
	 * sRem
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static Long sRem(String key, byte[] value) {
		return RedisUtils.sync(connect -> connect.srem(SafeEncoder.encode(key), value));
	}
	
	/**
	 * sLen
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static Long sLen(String key) {
		return RedisUtils.sync(connect -> connect.scard(SafeEncoder.encode(key)));
	}


	/**
	 * runScript
	 * 
	 * @param key
	 * @return
	 */
	public static <T> T runScript(String script, ScriptOutputType type, byte[][] values) {
		return RedisUtils.sync(connect -> connect.eval(script, type, values, values[0]));
	}
}