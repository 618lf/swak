package com.swak.common.cache.ehcache;

import com.swak.common.cache.AbstractCacheManager;
import com.swak.common.cache.Cache;

import net.sf.ehcache.Ehcache;

/**
 * 复写 EhCacheCacheManager
 * @author lifeng
 *
 */
public class EhCacheCacheManager extends AbstractCacheManager{

	private net.sf.ehcache.CacheManager cacheManager;

	public EhCacheCacheManager() {}

	public EhCacheCacheManager(net.sf.ehcache.CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void setCacheManager(net.sf.ehcache.CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public net.sf.ehcache.CacheManager getCacheManager() {
		return this.cacheManager;
	}

	/**
	 * 只能配置的方式创建，这里只是一个代理
	 */
	@Override
	public Cache createCache(String name, int timeToIdle) {
		Ehcache ehcache = this.cacheManager.getEhcache(name);
		return new EhCacheCache(ehcache);
	}
}