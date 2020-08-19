package com.swak.redis.getdel;

import org.junit.Test;

import com.swak.cache.AsyncCache;
import com.swak.cache.Cache;
import com.swak.cache.CacheManager;
import com.swak.redis.RedisCacheManager;
import com.swak.redis.RedisTest;

public class GetAndDel extends RedisTest {

	/**
	 * 得到并删除
	 */
	@Test
	public void getAndDel() {
		CacheManager cacheManager = new RedisCacheManager(redisService, null);
		Cache<String> _qrcodeCache = cacheManager.getCache("test", 60 * 5, false);
		AsyncCache<String> qrcodeCache = _qrcodeCache.async();
		System.out.println(qrcodeCache.getStringAndDel("1"));
	}
}
