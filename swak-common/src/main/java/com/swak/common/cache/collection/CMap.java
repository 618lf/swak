package com.swak.common.cache.collection;

public interface CMap<K, V> {

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
	
	/**
	 * 删除
	 * @param v
	 */
	void delete(K v);
}
