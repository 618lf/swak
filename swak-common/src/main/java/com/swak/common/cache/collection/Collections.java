package com.swak.common.cache.collection;

/**
 * 创建集合
 * @author lifeng
 */
public final class Collections {

	/**
	 * 普通的list
	 * @param name
	 * @return
	 */
	public static <T> CList<T> newList(String name) {
		return new ListCache<T>(name, -1);
	}
	
	/**
	 * 普通的Map
	 * @param name
	 * @return
	 */
	public static <T> CMap<String, T> newMap(String name) {
		return new MapCache<T>(name, -1);
	}
	
	/**
	 * 双重map
	 * @param name
	 * @return
	 */
	public static <T> MultiMap<String, T> newMultiMap(String name) {
		return new MultiMapCache<T>(name, -1);
	}
}