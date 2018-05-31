package com.swak.cache.collection;

import java.util.Map;

import reactor.core.publisher.Mono;

/**
 * 异步 MultiMap 操作
 * @author lifeng
 * @param <K>
 * @param <V>
 */
public interface ReactiveMultiMap<K, V> {

	/**
	 * 获得整个 Map
	 */
	Mono<Map<K, V>> get(K k);

	/**
	 * 添加整个 Map
	 */
	Mono<String> put(K k, Map<K, V> v);
	
	/**
	 * 删除
	 * @param v
	 */
	Mono<Long> delete(K v);

	/**
	 * 获得一个值
	 * @param k1
	 * @param k2
	 * @return
	 */
	Mono<V> get(K k1, K k2);
	
	/**
	 * 存储一个值
	 * @param k1
	 * @param k2
	 * @param v
	 */
	Mono<Boolean> pub(K k1, K k2, V v);
	
	/**
	 * 删除一个属性
	 * @param k1
	 * @param k2
	 */
	Mono<Long> delete(K k1, K k2);
	
	/**
	 * 设置过期时间
	 * @param seconds
	 * @return
	 */
	ReactiveMultiMap<K, V> expire(int seconds);
	
	/**
	 * 设置为原型类型的list
	 * @return
	 */
	ReactiveMultiMap<K, String> primitive();
	
	/**
	 * 设置为复杂类型的list
	 * @return
	 */
	ReactiveMultiMap<K, V> complex();
}
