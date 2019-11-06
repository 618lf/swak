package com.swak.cache.redis.operations;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import com.swak.cache.Cons;
import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.RedisUtils;

import io.lettuce.core.MapScanCursor;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.ValueScanCursor;

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
	 * incr
	 * 
	 * @param key
	 * @return
	 */
	public static CompletionStage<Long> incr(String key) {
		return RedisUtils.async(connect -> connect.incr(SafeEncoder.encode(key)));
	}

	/**
	 * decr
	 * 
	 * @param key
	 * @return
	 */
	public static CompletionStage<Long> decr(String key) {
		return RedisUtils.async(connect -> connect.decr(SafeEncoder.encode(key)));
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
	 * hscan
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static CompletionStage<MapScanCursor<byte[], byte[]>> hscan(String key, ScanCursor cursor, ScanArgs scanArgs) {
		return RedisUtils.async(connect -> connect.hscan(SafeEncoder.encode(key), cursor, scanArgs));
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
	 * sMembers
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static CompletionStage<Set<byte[]>> sMembers(String key) {
		return RedisUtils.async(connect -> connect.smembers(SafeEncoder.encode(key)));
	}

	/**
	 * sscan
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static CompletionStage<ValueScanCursor<byte[]>> sscan(String key, ScanCursor cursor, ScanArgs scanArgs) {
		return RedisUtils.async(connect -> connect.sscan(SafeEncoder.encode(key), cursor, scanArgs));
	}
	
	/**
	 * loadScript
	 * 
	 * @param key
	 * @return
	 */
	public static CompletionStage<String> loadScript(String script) {
		return RedisUtils.async(connect -> connect.scriptLoad(SafeEncoder.encode(script)));
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
	
	/**
	 * runScript -- 脚本已经通过 loadScript 安装好
	 * 
	 * @param key
	 * @return
	 */
	public static <T> CompletionStage<T> runShaScript(String script, ScriptOutputType type, byte[][] values) {
		return RedisUtils.async(connect -> connect.evalsha(script, type, values, values[0]));
	}
}
