package com.swak.common.cache.redis;

import com.swak.common.cache.AbstractCacheManager;
import com.swak.common.cache.Cache;
/**
 * 
 * 不需要事物支持的缓存管理
 * @author root
 *
 */
public class RedisCacheManager extends AbstractCacheManager {

	/**
	 * 以安全的方式创建一个缓存
	 */
	@Override
	public Cache createCache(String name, int timeToIdle) {
		RedisCache cache = new RedisCache(name);
		cache.setTimeToIdle(timeToIdle);
		return cache;
	}
}