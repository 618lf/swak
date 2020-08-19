package com.swak;

import com.swak.cache.Cache;
import com.swak.cache.CacheManager;
import com.swak.redis.NameableCache;

/**
 * 默认的缓存管理
 * 
 * @author root
 */
public abstract class AbstractCacheManager implements CacheManager {

	/**
	 * 获得一个缓存，如果不存在则创建一个默认的缓存
	 */
	@Override
	public <T> Cache<T> getCache(String name) {
		return this.getCache(name, NameableCache.DEFAULT_LIFE_TIME);
	}

	/**
	 * 指定参数创建缓存
	 */
	@Override
	public <T> Cache<T> getCache(String name, int timeToIdle) {
		return this.createCache(name, timeToIdle, NameableCache.DEFAULT_IDEA_LIFE);
	}

	/**
	 * 指定参数创建缓存
	 */
	@Override
	public <T> Cache<T> getCache(String name, int timeToIdle, boolean idleAble) {
		return this.createCache(name, timeToIdle, idleAble);
	}

	/**
	 * 创建一个缓存
	 * 
	 * @param name
	 * @param timeToIdle
	 * @return
	 */
	protected abstract <T> Cache<T> createCache(String name, int timeToIdle, boolean idleAble);
}