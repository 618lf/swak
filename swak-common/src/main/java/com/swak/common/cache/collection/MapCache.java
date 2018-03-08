package com.swak.common.cache.collection;

import com.swak.common.cache.Cache;
import com.swak.common.cache.redis.AbstractRedisCache;
import com.swak.common.cache.redis.RedisUtils;

/**
 * 是一个大Map
 * @author lifeng
 */
public class MapCache implements CMap<String, Object>{

	/**
	 * 所有的列表都使用这个作为KEY
	 */
	private static String DEFAULT_KEY = "_MAP";
	
	/**
	 * 底层用 Cache 来支持
	 */
	private Cache _cache;
	
	public MapCache(String name) {
		this(name, -1);
	}
	
	public MapCache(String name, int timeToIdle) {
		_cache = new AbstractRedisCache(name, timeToIdle) {
			
			@Override
			protected Object _get(String key) {
				return RedisUtils.getRedis().hGet(this.getKeyName(DEFAULT_KEY), key);
			}

			@Override
			protected void _set(String key, Object value, int expiration) {
				String _key = this.getKeyName(DEFAULT_KEY);
				RedisUtils.getRedis().hSet(_key, key, value);
				if (isValid(expiration)) {
					RedisUtils.getRedis().expire(_key, expiration);
				}
			}

			@Override
			protected void _expire(String key) {
				String keyName = this.getKeyName(DEFAULT_KEY);
				int expiration = this.getTimeToIdle();
				if (isValid(expiration)) {
					RedisUtils.getRedis().expire(keyName, expiration);
				}
			}
		};
	}

	@Override
	public <T> T get(String k) {
		return this._cache.get(k);
	}

	@Override
	public <T> void put(String k, T v) {
		this._cache.put(k, v);
	}

	@Override
	public void delete(String v) {
		this._cache.delete(v);
	}
}