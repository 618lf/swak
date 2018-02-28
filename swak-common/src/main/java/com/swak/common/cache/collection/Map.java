package com.swak.common.cache.collection;

public interface Map<K, V> {

	/**
	 * 获得值
	 * @param v
	 * @return
	 */
	<T> T get(K k);
	
	/**
	 * 存储值
	 * @param k
	 * @param v
	 */
	<T> void put(K k, T v);
}
