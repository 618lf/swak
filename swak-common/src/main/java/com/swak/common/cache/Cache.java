package com.swak.common.cache;

/**
 * 只提供最基本的缓存操作
 * @author lifeng
 */
public interface Cache<T> {
	
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
	T getObject(String key);
	
	/**
	 * 得到一个值
	 * @param key
	 * @return
	 */
	String getString(String key);
	
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
	void delete(String ... keys);
	
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
	void putObject(String key, T value);
	
	/**
	 * 添加key， 使用默认定义的时间
	 * @param key
	 * @param value
	 */
	void putString(String key, String value);
	
	/**
	 * 生存时间
	 * @param key
	 * @return
	 */
	long ttl(String key);
}