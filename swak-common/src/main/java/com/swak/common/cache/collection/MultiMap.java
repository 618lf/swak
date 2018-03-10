package com.swak.common.cache.collection;

import java.util.Map;

public interface MultiMap<K, V> {

	/**
	 * 获得整个 Map
	 */
	Map<K, V> get(K k);

	/**
	 * 添加整个 Map
	 */
	void put(K k, Map<K, V> v);
	
	/**
	 * 删除
	 * @param v
	 */
	void delete(K v);

	/**
	 * 获得一个值
	 * @param k1
	 * @param k2
	 * @return
	 */
	V get(K k1, K k2);
	
	/**
	 * 存储一个值
	 * @param k1
	 * @param k2
	 * @param v
	 */
	void pub(K k1, K k2, V v);
	
	/**
	 * 删除一个属性
	 * @param k1
	 * @param k2
	 */
	void delete(K k1, K k2);
}
