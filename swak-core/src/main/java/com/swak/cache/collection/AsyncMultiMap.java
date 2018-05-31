package com.swak.cache.collection;

import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface AsyncMultiMap<K, V> {

	/**
	 * 获得整个 Map
	 */
	CompletionStage<Map<K, V>> get(K k);

	/**
	 * 添加整个 Map
	 */
	CompletionStage<String> put(K k, Map<K, V> v);
	
	/**
	 * 删除
	 * @param v
	 */
	CompletionStage<Long> delete(K v);

	/**
	 * 获得一个值
	 * @param k1
	 * @param k2
	 * @return
	 */
	CompletionStage<V> get(K k1, K k2);
	
	/**
	 * 存储一个值
	 * @param k1
	 * @param k2
	 * @param v
	 */
	CompletionStage<Boolean> put(K k1, K k2, V v);
	
	/**
	 * 删除一个属性
	 * @param k1
	 * @param k2
	 */
	CompletionStage<Long> delete(K k1, K k2);
	
	/**
	 * 设置过期时间
	 * @param seconds
	 * @return
	 */
	AsyncMultiMap<K, V> expire(int seconds);
	
	/**
	 * 设置为原型类型的list
	 * @return
	 */
	AsyncMultiMap<K, String> primitive();
	
	/**
	 * 设置为复杂类型的list
	 * @return
	 */
	AsyncMultiMap<K, V> complex();
}
