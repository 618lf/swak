package com.swak.cache;

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
	<T> Cache<T> getCache(String name);
	
	/**
	 * 创建一个缓存
	 * @param name
	 * @param timeToIdle
	 * @return
	 */
	<T> Cache<T> getCache(String name, int timeToIdle);
	
	/**
	 * 创建一个缓存
	 * 
	 * @param name
	 * @param timeToIdle
	 * @param idleAble
	 * @return
	 */
	<T> Cache<T> getCache(String name, int timeToIdle, boolean idleAble);
	
	/**
	 * 获得本地缓存
	 * 
	 * @return
	 */
	LocalCache<Object> getLocalCache();
}
