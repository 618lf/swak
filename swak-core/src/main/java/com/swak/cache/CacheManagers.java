package com.swak.cache;

/**
 * 简单的获取 CacheManager
 * @author lifeng
 */
public class CacheManagers {
	
	/**
	 * 真实的缓存管理器
	 */
	private static CacheManager cacheManager;
	public static void setCacheManager(CacheManager cacheManager) {
		CacheManagers.cacheManager = cacheManager;
	}
	public static CacheManager manager() {
		return cacheManager;
	}
	
	/**
	 * 获得一个缓存， 内部会有一个缓存来存储
	 * @param key
	 * @return
	 */
	public static <T> Cache<T> getCache(String key) {
		return cacheManager.getCache(key);
	}
	
	/**
	 * 获得一个缓存， 内部会有一个缓存来存储
	 * @param key
	 * @return
	 */
	public static <T> Cache<T> getCache(String key, int timeToIdle) {
		return cacheManager.getCache(key, timeToIdle);
	}
}