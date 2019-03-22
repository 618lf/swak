package com.tmt;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import com.swak.cache.Cache;
import com.swak.cache.CacheManager;
import com.swak.test.ApplicationTest;
import com.swak.test.utils.MultiThreadTest;

/**
 * 系统测试的启动
 * 
 * @author lifeng
 */
@RunWith(SpringRunner.class)
@ApplicationTest
public class AppRunnerTest {

	@Autowired(required = false)
	private CacheManager cacheManager;

	@Test
	public void localCacheTest() throws InterruptedException {

		// 创建一个二级缓存
		Cache<Object> lfCache = cacheManager.getCache("lifeng").wrapLocal();
		
		CountDownLatch countDownLatch = new CountDownLatch(1);
		MultiThreadTest.run(() -> {
			while (true) {
				try {
					lfCache.putString("name", "ifeng");
					Thread.sleep(2000);
					lfCache.delete("name");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1, "测试存储数据");
		countDownLatch.await();
	}
}