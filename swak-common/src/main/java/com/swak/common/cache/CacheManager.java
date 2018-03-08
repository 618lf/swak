package com.swak.common.cache;

/**
 * 缓存管理器
 * @author root
 */
public interface CacheManager {

	/**
	 * 通过名称获得一个缓存
	 * @param name
	 * @return
	 */
	Cache getCache(String name);
	
	/**
	 * 创建一个缓存
	 * @param name
	 * @param timeToIdle
	 * @return
	 */
	Cache getCache(String name, int timeToIdle);
	
	/**
	 * 创建一个缓存
	 * @param name
	 * @param timeToIdle
	 * @return
	 */
	Cache createCache(String name, int timeToIdle);
	
	/**
	 * 包括缓存
	 * @param cache
	 * @return
	 */
	Cache wrap(Cache cache);
}
