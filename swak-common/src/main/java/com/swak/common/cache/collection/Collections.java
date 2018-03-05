package com.swak.common.cache.collection;

/**
 * 基于缓存的集合
 * @author lifeng
 */
public class Collections {
	
	/**
	 * 创建一个基于缓存的双Map
	 * @param name
	 * @param timeToIdle
	 * @return
	 */
	public static MultiMap<String, Object> newMultiMap(String name) {
		return new MultiMapCache(name);
	}

	/**
	 * 创建一个基于缓存的双Map
	 * @param name
	 * @param timeToIdle
	 * @return
	 */
	public static MultiMap<String, Object> newMultiMap(String name, int timeToIdle) {
		return new MultiMapCache(name, timeToIdle);
	}
}