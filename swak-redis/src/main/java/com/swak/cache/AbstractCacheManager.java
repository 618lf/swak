package com.swak.cache;

import com.swak.cache.redis.RedisCache;
import com.swak.cache.redis.RedisCacheChannel;
import com.swak.cache.redis.RedisLocalCache;

/**
 * 默认的缓存管理
 * @author root
 */
public abstract class AbstractCacheManager implements CacheManager {

	/**
	 * 获得本地缓存
	 * @return
	 */
	protected abstract RedisLocalCache getLocalCache();
	
	/**
	 * 获得一个缓存，如果不存在则创建一个默认的缓存
	 */
	@Override
	public <T> Cache<T> getCache(String name) {
		return this.getCache(name, -1);
	}
	
	/**
	 * 指定参数创建缓存
	 */
	@Override
	public <T> Cache<T> getCache(String name, int timeToIdle) {
		return this.createCache(name, timeToIdle);
	}
	
	/**
	 * 创建一个缓存
	 * @param name
	 * @param timeToIdle
	 * @return
	 */
	protected abstract <T> Cache<T> createCache(String name, int timeToIdle);

	/**
	 * 将 cache 包裹为二级缓存
	 */
	@Override
	public <T> Cache<T> wrap(Cache<T> cache) {
		if (cache == null || cache instanceof RedisCacheChannel) {
			throw new RuntimeException("cache 为一个普通的redis 缓存");
		}
		RedisLocalCache local = getLocalCache();
		RedisCacheChannel<T> channel = new RedisCacheChannel<T>();
		channel.setLocal(local);
		channel.setRemote((RedisCache<T>)cache);
		return channel;
	}
}