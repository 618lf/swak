package com.swak.common.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.FutureTask;

/**
 * 默认的缓存管理
 * @author root
 */
public abstract class AbstractCacheManager implements CacheManager {

	private final ConcurrentMap<String, FutureTask<Cache>> cacheMap = new ConcurrentHashMap<String, FutureTask<Cache>>(16);

	/**
	 * 获得一个缓存，如果不存在则创建一个默认的缓存
	 */
	@Override
	public Cache getCache(String name) {
		return this.getCache(name, -1);
	}
	
	/**
	 * 指定参数创建缓存
	 */
	@Override
	public Cache getCache(String name, int timeToIdle) {
		try {
			FutureTask<Cache> fCache = cacheMap.get(name);
			if (fCache != null) {
				return fCache.get();
			} else {
				fCache = new FutureTask<Cache>(() -> this.createCache(name, timeToIdle));
				FutureTask<Cache> _Cache = cacheMap.putIfAbsent(name, fCache);
				if (_Cache == null) {
					_Cache = fCache;
					fCache.run();
				}
				return fCache.get();
			}
		}catch(Exception e) {
			return null;
		}
	}
}