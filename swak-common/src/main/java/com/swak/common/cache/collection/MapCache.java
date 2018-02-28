package com.swak.common.cache.collection;

import com.swak.common.cache.redis.ExpireTimeValueWrapper;
import com.swak.common.cache.redis.RedisCache;
import com.swak.common.cache.redis.RedisUtils;

/**
 * 是一个大Map
 * @author lifeng
 */
public class MapCache extends RedisCache implements Map<String, Object>{

	@Override
	protected Object _get(String key) {
		return RedisUtils.hGet(this.getKeyName(key), key);
	}

	@Override
	protected void _set(String key, Object value, int expiration) {
		String _key = this.getKeyName(key);
		RedisUtils.hSet(_key, key, value);
		if (ExpireTimeValueWrapper.isValid(expiration)) {
			RedisUtils.expire(_key, expiration);
		}
	}

	/**
	 * 只是一个Map
	 */
	@Override
	protected String getKeyName(Object key) {
		return this.getName();
	}
}