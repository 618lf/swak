package com.swak.common.cache;

import java.util.List;

/**
 * 只提供最基本的缓存操作
 * @author lifeng
 */
public interface Cache {
	
	/**
	 * 缓存的名称
	 */
	String getName();
	
	/**
	 * 得到默认的缓存
	 * @return
	 */
	Object getNativeCache();
	
	/**
	 * 得到一个值
	 * @param key
	 * @return
	 */
	<T> T get(String key);
	
	/**
	 * 删除一个
	 * @param key
	 */
	void delete(String key);
	
	/**
	 * 删除一系列值
	 * @param keys
	 * @return
	 */
	void delete(List<String> keys);
	
	/**
	 * key 是否存在
	 * @param key
	 */
	boolean exists(String key);
	
	/**
	 * 添加key， 使用默认定义的时间
	 * @param key
	 * @param value
	 */
	void put(String key, Object value);
	
	/**
	 * 清除缓存
	 */
	void clear();
	
	/**
	 * 生存时间
	 * @param key
	 * @return
	 */
	long ttl(String key);
}