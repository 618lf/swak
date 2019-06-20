package com.swak.redis.getdel;

import org.junit.Test;

import com.swak.cache.AsyncCache;
import com.swak.cache.Cache;
import com.swak.cache.CacheManager;
import com.swak.cache.redis.RedisCacheManager;
import com.swak.redis.RedisTest;

public class GetAndDel extends RedisTest {

	/**
	 * 得到并删除
	 */
	@Test
	public void getAndDel() {
		CacheManager cacheManager = new RedisCacheManager(null);
		Cache<String> _qrcodeCache = cacheManager.getCache("test", 60 * 5, false);
		AsyncCache<String> qrcodeCache = _qrcodeCache.async();
		// qrcodeCache.putString("1", "1");
		qrcodeCache.getStringAndDel("1");
	}
}
