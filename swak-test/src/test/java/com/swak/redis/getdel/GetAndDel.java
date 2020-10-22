package com.swak.redis.getdel;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.swak.cache.AsyncCache;
import com.swak.cache.Cache;
import com.swak.cache.CacheManager;
import com.swak.redis.RedisCacheManager;
import com.swak.redis.RedisTest;

public class GetAndDel extends RedisTest {

	/**
	 * 得到并删除
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void getAndDel() throws InterruptedException {
		CacheManager cacheManager = new RedisCacheManager(redisService, null);
		Cache<String> _qrcodeCache = cacheManager.getCache("test", 60 * 5, false);
		AsyncCache<String> qrcodeCache = _qrcodeCache.async();
		qrcodeCache.getStringAndDel("1").whenComplete((r, e) -> {
			if (e != null) {
				e.printStackTrace();
			}
			System.out.println(r);
		});
		// 等待结束
		new CountDownLatch(1).await();
	}
}
