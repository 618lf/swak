package com.swak.common.cache.collection;

public interface MultiMap<K, V> extends Map<K, java.util.Map<K, V>> {

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
}
