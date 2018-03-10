package com.swak.common.cache.collection;

public interface CMap<K, T> {

	/**
	 * 获得值
	 * @param v
	 * @return
	 */
	T get(K k);
	
	/**
	 * 存储值
	 * @param k
	 * @param v
	 */
	void put(K k, T v);
	
	/**
	 * 删除
	 * @param k
	 */
	void delete(K k);
}
