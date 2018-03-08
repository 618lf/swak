package com.swak.common.cache.collection;

import com.swak.common.cache.Cache;
import com.swak.common.cache.redis.AbstractRedisCache;
import com.swak.common.cache.redis.RedisUtils;

/**
 * FIFO - 本身就是一个 list
 * 
 * @author lifeng
 */
public class ListCache<T> implements CList<T> {

	/**
	 * 所有的列表都使用这个作为KEY
	 */
	private static String DEFAULT_KEY = "_LIST";

	/**
	 * 底层用 Cache 来支持
	 */
	private Cache _cache;

	public ListCache(String name) {
		this(name, -1);
	}

	public ListCache(String name, int timeToIdle) {
		_cache = new AbstractRedisCache(name, timeToIdle) {
			@Override
			protected Object _get(String key) {
				String keyName = this.getKeyName(key);
				return RedisUtils.getRedis().lPop(keyName);
			}

			@Override
			protected void _set(String key, Object value, int expiration) {
				String keyName = this.getKeyName(key);
				RedisUtils.getRedis().lPush(keyName, value);
				if (isValid(expiration)) {
					RedisUtils.getRedis().expire(keyName, expiration);
				}
			}

			@Override
			protected void _expire(String key) {
				String keyName = this.getKeyName(key);
				int expiration = this.getTimeToIdle();
				if (isValid(expiration)) {
					RedisUtils.getRedis().expire(keyName, expiration);
				}
			}
		};
	}

	/**
	 * 插入数据
	 */
	@Override
	public void push(T t) {
		this._cache.put(DEFAULT_KEY, t);
	}

	/**
	 * 获得数据
	 */
	@Override
	public T pop() {
		return this._cache.get(DEFAULT_KEY);
	}
}