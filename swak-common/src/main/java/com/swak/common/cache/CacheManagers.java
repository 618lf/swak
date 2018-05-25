package com.swak.common.cache;

import com.swak.common.cache.redis.RedisCacheManager;

/**
 * 简单的获取 CacheManager
 * @author lifeng
 */
public class CacheManagers {
	
	/**
	 * 真实的缓存管理器
	 */
	private static RedisCacheManager cacheManager;
	public static void setCacheManager(RedisCacheManager cacheManager) {
		CacheManagers.cacheManager = cacheManager;
	}
	public static RedisCacheManager manager() {
		return cacheManager;
	}
	
	/**
	 * 获得一个缓存
	 * @param key
	 * @return
	 */
	public static <T> Cache<T> getCache(String key) {
		Cache<T> cache = cacheManager.getCache(key);
		return cache;
	}
}