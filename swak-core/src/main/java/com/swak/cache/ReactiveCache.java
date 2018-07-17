package com.swak.cache;

import reactor.core.publisher.Mono;

/**
 * 响应式 cache
 * @author lifeng
 */
public interface ReactiveCache<T> {

	/**
	 * 缓存的名称
	 */
	String getName();
	
	/**
	 * 过期时间
	 * @param seconds
	 */
    default void setTimeToIdle(int seconds) {}
	
	/**
	 * 得到默认的缓存
	 * @return
	 */
	default Object getNativeCache() {
		return this;
	}
	
	/**
	 * 得到一个值
	 * @param key
	 * @return
	 */
	Mono<T> getObject(String key);
	
	/**
	 * 得到一个值
	 * @param key
	 * @return
	 */
	Mono<String> getString(String key);
	
	/**
	 * 删除一个
	 * @param key
	 */
	Mono<Long> delete(String key);
	
	/**
	 * 删除一系列值
	 * @param keys
	 * @return
	 */
	Mono<Long> delete(String ... keys);
	
	/**
	 * key 是否存在
	 * @param key
	 */
	Mono<Long> exists(String key);
	
	/**
	 * 添加key， 使用默认定义的时间
	 * @param key
	 * @param value
	 */
	Mono<Entity<T>> putObject(String key, T value);
	
	/**
	 * 添加key， 使用默认定义的时间
	 * @param key
	 * @param value
	 */
	Mono<Entity<String>> putString(String key, String value);
	
	/**
	 * 生存时间
	 * @param key
	 * @return
	 */
	Mono<Long> ttl(String key);
}
