package com.swak.cache;

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
	Long delete(String key);
	
	/**
	 * 删除一系列值
	 * @param keys
	 * @return
	 */
	Long delete(String ... keys);
	
	/**
	 * key 是否存在
	 * @param key
	 */
	Boolean exists(String key);
	
	/**
	 * 添加key， 使用默认定义的时间
	 * @param key
	 * @param value
	 */
	Entity<T> putObject(String key, T value);
	
	/**
	 * 添加key， 使用默认定义的时间
	 * @param key
	 * @param value
	 */
	Entity<String> putString(String key, String value);
	
	/**
	 * 生存时间
	 * @param key
	 * @return
	 */
	Long ttl(String key);
	
	/**
	 * 转为异步
	 * @return
	 */
	default AsyncCache<T> async() {
		return null;
	}
	
	/**
	 * 转为响应式
	 * @return
	 */
	default ReactiveCache<T> reactive() {
		return null;
	}
	
	/**
	 * 转为二级缓存
	 * 
	 * @return
	 */
	default Cache<T> wrapLocal() {
		return null;
	}
}