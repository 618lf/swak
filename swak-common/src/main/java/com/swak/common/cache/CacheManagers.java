package com.swak.common.cache;

import com.swak.common.cache.redis.RedisCacheManager;

/**
 * 简单的获取 CacheManager
 * @author lifeng
 */
public class CacheManagers {
	private static RedisCacheManager cacheManager;
	public static void setCacheManager(RedisCacheManager cacheManager) {
		CacheManagers.cacheManager = cacheManager;
	}
	public static RedisCacheManager manager() {
		return cacheManager;
	}
}