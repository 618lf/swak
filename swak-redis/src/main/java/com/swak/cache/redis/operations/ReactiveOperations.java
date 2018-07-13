package com.swak.cache.redis.operations;

import java.util.Map;

import com.swak.cache.Cons;
import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.RedisUtils;

import io.lettuce.core.ScriptOutputType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 一组响应式的操作API
 * @author lifeng
 *
 */
public class ReactiveOperations {

	/**
	 * ttl 
	 * @param key
	 * @return
	 */
	public static Mono<Long> ttl(String key) {
		return RedisUtils.reactive(connect -> connect.ttl(SafeEncoder.encode(key)));
	}
	
	/**
	 * get 
	 * @param key
	 * @return
	 */
	public static Mono<byte[]> get(String key) {
		return RedisUtils.reactive(connect -> connect.get(SafeEncoder.encode(key)));
	}
	
	/**
	 * set 
	 * @param key
	 * @return
	 */
	public static Mono<String> set(String key, byte[] value) {
		return RedisUtils.reactive(connect -> connect.set(SafeEncoder.encode(key), value));
	}
	
	/**
	 * set 
	 * @param key
	 * @return
	 */
	public static Mono<String> set(String key, byte[] value, int expire) {
		String script = Cons.PUT_LUA;
		byte[][] values = new byte[][] {SafeEncoder.encode(key), value, SafeEncoder.encode(String.valueOf(expire))};
		return Mono.from(ReactiveOperations.runScript(script, ScriptOutputType.INTEGER, values)).map(s -> key);
	}
	
	/**
	 * del 
	 * @param key
	 * @return
	 */
	public static Mono<Long> del(String ... keys) {
		return RedisUtils.reactive(connect -> connect.del(SafeEncoder.encodeMany(keys)));
	}
	
	/**
	 * del 
	 * @param key
	 * @return
	 */
	public static Mono<Boolean> expire(String key, int expire) {
		return RedisUtils.reactive(connect -> connect.expire(SafeEncoder.encode(key), expire));
	}
	
	/**
	 * del 
	 * @param key
	 * @return
	 */
	public static Mono<Long> exists(String ... key) {
		return RedisUtils.reactive(connect -> connect.exists(SafeEncoder.encodeMany(key)));
	}
	
	/**
	 * lPush
	 * @param key
	 * @param value
	 * @return
	 */
	public static Mono<Long> lPush(String key, byte[] ... value) {
		return RedisUtils.reactive(connect -> connect.lpush(SafeEncoder.encode(key), value));
	}
	
	/**
	 * lPop
	 * @param key
	 * @param value
	 * @return
	 */
	public static Mono<byte[]> lPop(String key) {
		return RedisUtils.reactive(connect -> connect.lpop(SafeEncoder.encode(key)));
	}
	
	/**
	 * lPop
	 * @param key
	 * @param value
	 * @return
	 */
	public static Mono<Long> lLen(String key) {
		return RedisUtils.reactive(connect -> connect.llen(SafeEncoder.encode(key)));
	}
	
	/**
	 * hget
	 * @param key
	 * @param field
	 * @return
	 */
	public static Mono<byte[]> hGet(String key, String field) {
		return RedisUtils.reactive(connect -> connect.hget(SafeEncoder.encode(key), SafeEncoder.encode(field)));
	}
	
	/**
	 * hset
	 * @param key
	 * @param field
	 * @return
	 */
	public static Mono<Boolean> hSet(String key, String field, byte[] value) {
		return RedisUtils.reactive(connect -> connect.hset(SafeEncoder.encode(key), SafeEncoder.encode(field), value));
	}
	
	/**
	 * hdel
	 * @param key
	 * @param field
	 * @return
	 */
	public static Mono<Long> hDel(String key, String ... fields) {
		return RedisUtils.reactive(connect -> connect.hdel(SafeEncoder.encode(key), SafeEncoder.encodeMany(fields)));
	}
	
	/**
	 * hGetAll
	 * @param key
	 * @return
	 */
	public static Mono<Map<byte[], byte[]>> hGetAll(String key) {
		return RedisUtils.reactive(connect -> connect.hgetall(SafeEncoder.encode(key))) ;
	}
	
	/**
	 * hMset
	 * @param key
	 * @param map
	 * @return
	 */
	public static Mono<String> hMSet(String key, Map<byte[], byte[]> map) {
		return RedisUtils.reactive(connect -> connect.hmset(SafeEncoder.encode(key), map));
	}
	
	/**
	 * runScript
	 * @param key
	 * @return
	 */
	public static <T> Flux<T> runScript(String script, ScriptOutputType type, byte[][] values) {
		return RedisUtils.reactive(connect -> connect.eval(script, type, values, values[0]));
	}
}
