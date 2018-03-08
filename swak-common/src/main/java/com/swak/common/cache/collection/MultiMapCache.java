package com.swak.common.cache.collection;

import java.util.Map;

import com.swak.common.cache.redis.AbstractRedisCache;
import com.swak.common.cache.redis.RedisUtils;

/**
 * value 是 一个 map
 * @author lifeng
 */
public class MultiMapCache extends AbstractRedisCache implements MultiMap<String, Object>{

	/**
	 * 无过期时间
	 * @param name
	 */
	public MultiMapCache(String name) {
		super(name);
	}
	
	/**
	 * 指定过期时间
	 * @param name
	 * @param timeToIdle
	 */
	public MultiMapCache(String name, int timeToIdle) {
		super(name, timeToIdle);
	}

	@Override
	protected Object _get(String key) {
		String _key = this.getKeyName(key);
		return RedisUtils.getRedis().hGetAll(_key);
	}

	/**
	 * 必须是这只Map
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void _set(String key, Object value, int expiration) {
		String _key = this.getKeyName(key);
		RedisUtils.getRedis().hMSet(_key, (Map<String, Object>)value);
		if (isValid(expiration)) {
			RedisUtils.getRedis().expire(_key, expiration);
		}
	}
	
	/**
	 * 设置过期时间
	 * 
	 * @param key
	 */
	protected void _expire(String key) {
		String keyName = this.getKeyName(key);
		int expiration = this.getTimeToIdle();
		if (isValid(expiration)) {
			RedisUtils.getRedis().expire(keyName, expiration);
		}
	}

	@Override
	public Object get(String k1, String k2) {
		return RedisUtils.getRedis().hGet(this.getKeyName(k1), k2);
	}

	@Override
	public void pub(String k1, String k2, Object v) {
		RedisUtils.getRedis().hSet(this.getKeyName(k1), k2, v);
	}

	@Override
	public void delete(String k1, String k2) {
		RedisUtils.getRedis().hDel(this.getKeyName(k1), k2);
	}
}