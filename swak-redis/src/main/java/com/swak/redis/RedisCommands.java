package com.swak.redis;

import java.util.Map;

import io.lettuce.core.ScriptOutputType;

/**
 * 同步操作命令
 * 
 * @author lifeng
 * @date 2020年8月19日 下午4:47:30
 */
public interface RedisCommands<K, V> {

	/**
	 * ttl
	 * 
	 * @param key
	 * @return
	 */
	long ttl(String key);

	/**
	 * get
	 * 
	 * @param key
	 * @return
	 */
	byte[] get(String key);

	/**
	 * set
	 * 
	 * @param key
	 * @return
	 */
	String set(String key, byte[] value);

	/**
	 * set
	 * 
	 * @param key
	 * @return
	 */
	String set(String key, byte[] value, int expire);

	/**
	 * del
	 * 
	 * @param key
	 * @return
	 */
	long del(String... keys);

	/**
	 * expire
	 * 
	 * @param key
	 * @return
	 */
	boolean expire(String key, int expire);

	/**
	 * exists
	 * 
	 * @param key
	 * @return
	 */
	Long exists(String... key);

	/**
	 * incr
	 * 
	 * @param key
	 * @return
	 */
	Long incr(String key);

	/**
	 * decr
	 * 
	 * @param key
	 * @return
	 */
	Long decr(String key);

	/**
	 * lPush
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	long lPush(String key, byte[]... value);

	/**
	 * lGet 只获取不删除
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	byte[] lGet(String key);

	/**
	 * lPop
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	byte[] lPop(String key);

	/**
	 * rPush
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	long rPush(String key, byte[]... value);

	/**
	 * rGet 只获取不删除
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	byte[] rGet(String key);

	/**
	 * rPop
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	byte[] rPop(String key);

	/**
	 * lPop
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	long lLen(String key);

	/**
	 * hget
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	byte[] hGet(String key, String field);

	/**
	 * hset
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	boolean hSet(String key, String field, byte[] value);

	/**
	 * hdel
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	long hDel(String key, String... fields);

	/**
	 * hGetAll
	 * 
	 * @param key
	 * @return
	 */
	Map<byte[], byte[]> hGetAll(String key);

	/**
	 * hMset
	 * 
	 * @param key
	 * @param map
	 * @return
	 */
	String hMSet(String key, Map<byte[], byte[]> map);

	/**
	 * sAdd
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	boolean sAdd(String key, byte[] value);

	/**
	 * sAdd
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	boolean sExists(String key, byte[] value);

	/**
	 * sRem
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	Long sRem(String key, byte[] value);

	/**
	 * sLen
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	Long sLen(String key);

	/**
	 * runScript
	 * 
	 * @param key
	 * @return
	 */
	<T> T runScript(String script, ScriptOutputType type, byte[][] values);
}
