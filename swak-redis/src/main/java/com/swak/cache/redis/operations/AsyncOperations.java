package com.swak.cache.redis.operations;

import java.util.Map;
import java.util.concurrent.CompletionStage;

import com.swak.cache.Cons;
import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.RedisUtils;

import io.lettuce.core.ScriptOutputType;

/**
 * 提供一组一步操作Api
 * 
 * @author lifeng
 */
public class AsyncOperations {

	/**
	 * ttl
	 * 
	 * @param key
	 * @return
	 */
	public static CompletionStage<Long> ttl(String key) {
		return RedisUtils.async(connect -> connect.ttl(SafeEncoder.encode(key)));
	}

	/**
	 * get
	 * 
	 * @param key
	 * @return
	 */
	public static CompletionStage<byte[]> get(String key) {
		return RedisUtils.async(connect -> connect.get(SafeEncoder.encode(key)));
	}

	/**
	 * set
	 * 
	 * @param key
	 * @return
	 */
	public static CompletionStage<String> set(String key, byte[] value) {
		return RedisUtils.async(connect -> connect.set(SafeEncoder.encode(key), value));
	}

	/**
	 * setnx
	 * 
	 * @param key
	 * @return
	 */
	public static CompletionStage<Boolean> setnx(String key, byte[] value) {
		return RedisUtils.async(connect -> connect.setnx(SafeEncoder.encode(key), value));
	}

	/**
	 * set
	 * 
	 * @param key
	 * @return
	 */
	public static CompletionStage<String> set(String key, byte[] value, int expire) {
		String script = Cons.PUT_LUA;
		byte[][] values = new byte[][] { SafeEncoder.encode(key), value, SafeEncoder.encode(String.valueOf(expire)) };
		return AsyncOperations.runScript(script, ScriptOutputType.INTEGER, values).thenApply(s -> key);
	}

	/**
	 * del
	 * 
	 * @param key
	 * @return
	 */
	public static CompletionStage<Long> del(String... keys) {
		return RedisUtils.async(connect -> connect.del(SafeEncoder.encodeMany(keys)));
	}

	/**
	 * del
	 * 
	 * @param key
	 * @return
	 */
	public static CompletionStage<Boolean> expire(String key, int expire) {
		return RedisUtils.async(connect -> connect.expire(SafeEncoder.encode(key), expire));
	}

	/**
	 * del
	 * 
	 * @param key
	 * @return
	 */
	public static CompletionStage<Long> exists(String... key) {
		return RedisUtils.async(connect -> connect.exists(SafeEncoder.encodeMany(key)));
	}

	/**
	 * lPush
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static CompletionStage<Long> lPush(String key, byte[]... value) {
		return RedisUtils.async(connect -> connect.lpush(SafeEncoder.encode(key), value));
	}

	/**
	 * lPop
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static CompletionStage<byte[]> lPop(String key) {
		return RedisUtils.async(connect -> connect.lpop(SafeEncoder.encode(key)));
	}

	/**
	 * lPop
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static CompletionStage<Long> lLen(String key) {
		return RedisUtils.async(connect -> connect.llen(SafeEncoder.encode(key)));
	}

	/**
	 * hget
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public static CompletionStage<byte[]> hGet(String key, String field) {
		return RedisUtils.async(connect -> connect.hget(SafeEncoder.encode(key), SafeEncoder.encode(field)));
	}

	/**
	 * hset
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public static CompletionStage<Boolean> hSet(String key, String field, byte[] value) {
		return RedisUtils.async(connect -> connect.hset(SafeEncoder.encode(key), SafeEncoder.encode(field), value));
	}

	/**
	 * hdel
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public static CompletionStage<Long> hDel(String key, String... fields) {
		return RedisUtils.async(connect -> connect.hdel(SafeEncoder.encode(key), SafeEncoder.encodeMany(fields)));
	}

	/**
	 * hGetAll
	 * 
	 * @param key
	 * @return
	 */
	public static CompletionStage<Map<byte[], byte[]>> hGetAll(String key) {
		return RedisUtils.async(connect -> connect.hgetall(SafeEncoder.encode(key)));
	}

	/**
	 * hMset
	 * 
	 * @param key
	 * @param map
	 * @return
	 */
	public static CompletionStage<String> hMSet(String key, Map<byte[], byte[]> map) {
		return RedisUtils.async(connect -> connect.hmset(SafeEncoder.encode(key), map));
	}

	/**
	 * sAdd
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static CompletionStage<Boolean> sAdd(String key, byte[] value) {
		return RedisUtils.async(connect -> connect.sadd(SafeEncoder.encode(key), value))
				.thenApply(res -> res != null && res > 0);
	}

	/**
	 * sAdd
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static CompletionStage<Boolean> sExists(String key, byte[] value) {
		return RedisUtils.async(connect -> connect.sismember(SafeEncoder.encode(key), value));
	}

	/**
	 * sRem
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static CompletionStage<Long> sRem(String key, byte[] value) {
		return RedisUtils.async(connect -> connect.srem(SafeEncoder.encode(key), value));
	}

	/**
	 * sLen
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static CompletionStage<Long> sLen(String key) {
		return RedisUtils.async(connect -> connect.scard(SafeEncoder.encode(key)));
	}

	/**
	 * runScript
	 * 
	 * @param key
	 * @return
	 */
	public static <T> CompletionStage<T> runScript(String script, ScriptOutputType type, byte[][] values) {
		return RedisUtils.async(connect -> connect.eval(script, type, values, values[0]));
	}
}
