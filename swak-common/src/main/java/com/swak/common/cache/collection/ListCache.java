package com.swak.common.cache.collection;

import com.swak.common.cache.redis.ExpireTimeValueWrapper;
import com.swak.common.cache.redis.RedisCache;
import com.swak.common.cache.redis.RedisUtils;

/**
 * FIFO - 本身就是一个 list
 * @author lifeng
 */
public class ListCache extends RedisCache implements List<Object>{

	
	@Override
	protected Object _get(String key) {
		String _key = this.getKeyName(key);
		return RedisUtils.getRedis().lPop(_key);
	}

	@Override
	protected void _set(String key, Object value, int expiration) {
		String _key = this.getKeyName(key);
		RedisUtils.getRedis().lPush(_key, value);
		if (ExpireTimeValueWrapper.isValid(expiration)) {
			RedisUtils.getRedis().expire(_key, expiration);
		}
	}

	@Override
	public void push(Object t) {
		this.put("", t);
	}

	@Override
	public Object pop() {
		return this.get("");
	}

	/**
	 * 名称直接就是 队列的名称
	 */
	@Override
	protected String getKeyName(Object key) {
		return this.getName();
	}
}