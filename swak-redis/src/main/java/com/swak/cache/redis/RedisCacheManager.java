package com.swak.cache.redis;

import com.swak.cache.AbstractCacheManager;
import com.swak.cache.Cache;
import com.swak.cache.LocalCache;

/**
 * 
 * 不需要事物支持的缓存管理
 * 
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
	 * 创建一个缓存
	 */
	@Override
	public <T> Cache<T> createCache(String name, int timeToIdle, boolean idleAble) {
		return new RedisCache<T>(name, timeToIdle, idleAble).setCacheManager(this);
	}

	/**
	 * 返回二级缓存
	 * 
	 * @return
	 */
	@Override
	public LocalCache<Object> getLocalCache() {
		return localCache;
	}
}