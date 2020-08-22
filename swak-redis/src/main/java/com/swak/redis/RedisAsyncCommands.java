package com.swak.redis;

import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * 异步操作命令
 * 
 * @author lifeng
 * @date 2020年8月19日 下午4:47:59
 */
public interface RedisAsyncCommands<K, V> {

	/**
	 * ttl
	 * 
	 * @param key
	 * @return
	 */
	CompletionStage<Long> ttl(String key);

	/**
	 * get
	 * 
	 * @param key
	 * @return
	 */
	CompletionStage<byte[]> get(String key);

	/**
	 * set
	 * 
	 * @param key
	 * @return
	 */
	CompletionStage<String> set(String key, byte[] value);

	/**
	 * setnx
	 * 
	 * @param key
	 * @return
	 */
	CompletionStage<Boolean> setnx(String key, byte[] value);

	/**
	 * set
	 * 
	 * @param key
	 * @return
	 */
	CompletionStage<String> set(String key, byte[] value, int expire);

	/**
	 * set
	 * 
	 * @param key
	 * @return
	 */
	CompletionStage<String> set(String key, byte[] value, Expiration expiration, SetOption setOption);

	/**
	 * del
	 * 
	 * @param key
	 * @return
	 */
	CompletionStage<Long> del(String... keys);

	/**
	 * del
	 * 
	 * @param key
	 * @return
	 */
	CompletionStage<Boolean> expire(String key, int expire);

	/**
	 * del
	 * 
	 * @param key
	 * @return
	 */
	CompletionStage<Long> exists(String... key);

	/**
	 * incr
	 * 
	 * @param key
	 * @return
	 */
	CompletionStage<Long> incr(String key);

	/**
	 * decr
	 * 
	 * @param key
	 * @return
	 */
	CompletionStage<Long> decr(String key);

	/**
	 * lPush
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	CompletionStage<Long> lPush(String key, byte[]... value);

	/**
	 * lPop
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	CompletionStage<byte[]> lPop(String key);

	/**
	 * lPop
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	CompletionStage<Long> lLen(String key);

	/**
	 * hget
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	CompletionStage<byte[]> hGet(String key, String field);

	/**
	 * hset
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	CompletionStage<Boolean> hSet(String key, String field, byte[] value);

	/**
	 * hdel
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	CompletionStage<Long> hDel(String key, String... fields);

	/**
	 * hGetAll
	 * 
	 * @param key
	 * @return
	 */
	CompletionStage<Map<byte[], byte[]>> hGetAll(String key);

	/**
	 * hMset
	 * 
	 * @param key
	 * @param map
	 * @return
	 */
	CompletionStage<String> hMSet(String key, Map<byte[], byte[]> map);

//	/**
//	 * hscan
//	 * 
//	 * @param key
//	 * @param value
//	 * @return
//	 */
//	CompletionStage<Cursor<Map.Entry<byte[], byte[]>>> hscan(String key, ScanOptions options);

	/**
	 * sAdd
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	CompletionStage<Boolean> sAdd(String key, byte[] value);

	/**
	 * sAdd
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	CompletionStage<Boolean> sExists(String key, byte[] value);

	/**
	 * sRem
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	CompletionStage<Long> sRem(String key, byte[] value);

	/**
	 * sLen
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	CompletionStage<Long> sLen(String key);

//	/**
//	 * sscan
//	 * 
//	 * @param key
//	 * @param value
//	 * @return
//	 */
//	CompletionStage<Cursor<byte[]>> sscan(String key, ScanOptions scanArgs);

	/**
	 * loadScript
	 * 
	 * @param key
	 * @return
	 */
	CompletionStage<String> loadScript(String script);

	/**
	 * runScript
	 * 
	 * @param key
	 * @return
	 */
	<T> CompletionStage<T> runScript(String script, ReturnType type, byte[][] values, byte[][] params);

	/**
	 * runScript -- 脚本已经通过 loadScript 安装好
	 * 
	 * @param key
	 * @return
	 */
	<T> CompletionStage<T> runShaScript(String script, ReturnType type, byte[][] values, byte[][] params);
}