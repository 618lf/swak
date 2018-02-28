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
	public String getName();
	
	/**
	 * 得到默认的缓存
	 * @return
	 */
	public Object getNativeCache();
	
	/**
	 * 得到一个值
	 * @param key
	 * @return
	 */
	public <T> T get(String key);
	
	/**
	 * 删除一个
	 * @param key
	 */
	public void delete(String key);
	
	/**
	 * 删除一系列值
	 * @param keys
	 * @return
	 */
	public void delete(List<String> keys);
	
	/**
	 * key 是否存在
	 * @param key
	 */
	public boolean exists(String key);
	
	/**
	 * 添加key， 使用默认定义的时间
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value);
	
	/**
	 * 清除缓存
	 */
	void clear();
	
	/**
	 * 生存时间
	 * @param key
	 * @return
	 */
	public long ttl(String key);
}