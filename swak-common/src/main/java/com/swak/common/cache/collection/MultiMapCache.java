package com.swak.common.cache.collection;

import java.util.Map;

import com.swak.common.cache.redis.ExpireTimeValueWrapper;
import com.swak.common.cache.redis.RedisCache;
import com.swak.common.cache.redis.RedisUtils;

/**
 * value 是 一个 map
 * @author lifeng
 */
public class MultiMapCache extends RedisCache implements MultiMap<String, Object>{

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
		if (ExpireTimeValueWrapper.isValid(expiration)) {
			RedisUtils.getRedis().expire(_key, expiration);
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
}