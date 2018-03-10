package com.swak.common.cache.collection;

import com.swak.common.cache.redis.NameableCache;
import com.swak.common.cache.redis.RedisUtils;

/**
 * 是一个大Map
 * @author lifeng
 */
public abstract class MapCache<T> extends NameableCache implements CMap<String, T>{

	/**
	 * 所有的列表都使用这个作为KEY
	 */
	private static String DEFAULT_KEY = "_MAP";
	
	public MapCache(String name) {
		this(name, -1);
	}
	
	public MapCache(String name, int timeToIdle) {
		super(name, timeToIdle);
	}

	@Override
	public T get(String k) {
		this.expire(null);
		return this.deserialize(RedisUtils.getRedis().hGet(this.getKeyName(null), k));
	}

	@Override
	public void put(String k, T v) {
		this.expire(null);
		RedisUtils.getRedis().hSet(this.getKeyName(null), k, this.serialize(v));
	}

	@Override
	public void delete(String k) {
		this.expire(null);
		RedisUtils.getRedis().hDel(this.getKeyName(null), k);
	}
	
	/**
	 * 序例化的方式
	 * @param t
	 * @return
	 */
	protected abstract byte[] serialize(T t);
	
	/**
	 * 序例化的方式
	 * @param t
	 * @return
	 */
	protected abstract T deserialize(byte[] bytes);
	
	/**
	 * redis list 的名称
	 * @return
	 */
	protected String getKeyName(String key) {
		return super.getKeyName(DEFAULT_KEY);
	}
}