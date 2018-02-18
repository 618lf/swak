package com.swak.common.cache;

import java.util.List;

/**
 * 在 spring cache 的基础上添加一些基础的功能: keys
 * 
 * 建议： key 使用字符串
 * 
 * @author lifeng
 *
 */
public interface Cache{
	
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
	 * 返回所有的key -- 表达式
	 * @return
	 */
	public List<Object> keys(String pattern);
	
	/**
	 * 返回当前缓存下的所有的key:指定前缀:pattern
	 * @param pattern
	 * @return
	 */
	public <T> List<T> values(String pattern);
	
	/**
	 * 得到一个值
	 * @param key
	 * @return
	 */
	public <T> T get(Object key);
	
	/**
	 * 删除一个
	 * @param key
	 */
	public void delete(Object key);
	
	/**
	 * 删除
	 * @param key
	 */
	public void evict(Object key);
	
	/**
	 * 删除  pattern 匹配的值
	 * @param pattern
	 */
	public void deletePattern(String pattern);
	
	/**
	 * 删除一系列值
	 * @param keys
	 * @return
	 */
	public void delete(List<Object> keys);
	
	/**
	 * key 是否存在
	 * @param key
	 */
	public boolean exists(Object key);
	
	/**
	 * 添加key， 使用默认定义的时间
	 * @param key
	 * @param value
	 */
	public void put(Object key, Object value);
	
	/**
	 * 添加key，动态设置当前值的过期时间
	 * timeToLive 和  timeToIdle 只能使用一个
	 * @param key
	 * @param value
	 * @param timeToLive -- 存活时间 (秒)
	 * @param timeToIdle -- 空闲时间  (秒)
	 */
	public void put(Object key, Object value, int timeToLive);
	
	
	/**
	 * Remove all mappings from the cache.
	 */
	void clear();
	
	/**
	 * 返回当前缓存下的所有的value
	 * @return
	 */
	public <T> List<T> values();
	
	/**
	 * 返回所有的key
	 * @return
	 */
	public List<Object> keys();
	
	/**
	 * 缓存大小
	 * @return
	 */
	public long size();
	
	/**
	 * 生存时间
	 * @param key
	 * @return
	 */
	public long ttl(Object key);
}