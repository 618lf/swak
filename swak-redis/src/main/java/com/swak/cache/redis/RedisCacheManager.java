package com.swak.cache.redis;

import com.swak.cache.AbstractCacheManager;
import com.swak.cache.Cache;
/**
 * 
 * 不需要事物支持的缓存管理
 * @author root
 *
 */
public class RedisCacheManager extends AbstractCacheManager {

	/**
	 * 设置二级缓存
	 */
	private final RedisLocalCache localCache;
	public RedisCacheManager(RedisLocalCache localCache) {
		this.localCache = localCache;
	}
	
	/**
	 * 以安全的方式创建一个缓存
	 */
	@Override
	public <T> Cache<T> createCache(String name, int timeToIdle) {
		RedisCache<T> cache = new RedisCache<T>(name);
		cache.setTimeToIdle(timeToIdle);
		return cache;
	}

	@Override
	protected RedisLocalCache getLocalCache() {
		return localCache;
	}
}