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
	
	/**
	 * 设置过期时间
	 * @param seconds
	 * @return
	 */
	CMap<K, T> expire(int seconds);
	
	/**
	 * 设置为原型类型的list
	 * @return
	 */
	CMap<K, String> primitive();
	
	/**
	 * 设置为复杂类型的list
	 * @return
	 */
	CMap<K, T> complex();
}
