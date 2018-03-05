package com.swak.common.cache.collection;

import java.util.Map;

public interface MultiMap<K, V> extends CMap<K, Map<K, V>> {

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
