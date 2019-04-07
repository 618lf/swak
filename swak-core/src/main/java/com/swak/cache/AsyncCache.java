package com.swak.cache;

import java.util.concurrent.CompletionStage;

/**
 * 异步 cache
 * @author lifeng
 * @param <T>
 */
public interface AsyncCache<T> {

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
	CompletionStage<T> getObject(String key);
	
	/**
	 * 得到一个值
	 * @param key
	 * @return
	 */
	CompletionStage<T> getObjectAndDel(String key);
	
	/**
	 * 得到一个值
	 * @param key
	 * @return
	 */
	CompletionStage<String> getString(String key);
	
	/**
	 * 得到一个值并删除
	 * @param key
	 * @return
	 */
	CompletionStage<String> getStringAndDel(String key);
	
	/**
	 * 删除一个
	 * @param key
	 */
	CompletionStage<Long> delete(String key);
	
	/**
	 * 删除一系列值
	 * @param keys
	 * @return
	 */
	CompletionStage<Long> delete(String ... keys);
	
	/**
	 * key 是否存在
	 * @param key
	 */
	CompletionStage<Long> exists(String key);
	
	/**
	 * 添加key， 使用默认定义的时间
	 * @param key
	 * @param value
	 */
	CompletionStage<Entity<T>> putObject(String key, T value);
	
	/**
	 * 添加key， 使用默认定义的时间
	 * @param key
	 * @param value
	 */
	CompletionStage<Entity<String>> putString(String key, String value);
	
	/**
	 * 生存时间
	 * @param key
	 * @return
	 */
	CompletionStage<Long> ttl(String key);
	
	/**
	 * 转为二级缓存
	 * 
	 * @return
	 */
	default AsyncCache<T> wrapLocal() {
		return null;
	}
}