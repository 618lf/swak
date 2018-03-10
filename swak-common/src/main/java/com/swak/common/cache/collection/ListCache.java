package com.swak.common.cache.collection;

import com.swak.common.cache.redis.NameableCache;
import com.swak.common.cache.redis.RedisUtils;

/**
 * FIFO - 本身就是一个 list
 * 
 * @author lifeng
 */
public abstract class ListCache<T> extends NameableCache implements CList<T> {

	/**
	 * 所有的列表都使用这个作为KEY
	 */
	private static String DEFAULT_KEY = "_LIST";

	public ListCache(String name) {
		this(name, -1);
	}
	
	public ListCache(String name, int timeToIdle) {
		super(name, timeToIdle);
	}

	/**
	 * 插入数据
	 */
	@Override
	public void push(T t) {
		this.expire(null);
		RedisUtils.getRedis().lPush(this.getKeyName(null), this.serialize(t));
	}

	/**
	 * 获得数据
	 */
	@Override
	public T pop() {
		this.expire(null);
		return this.deserialize(RedisUtils.getRedis().lPop(this.getKeyName(null)));
	}

	/**
	 * redis list 的名称
	 * @return
	 */
	protected String getKeyName(String key) {
		return super.getKeyName(DEFAULT_KEY);
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
}