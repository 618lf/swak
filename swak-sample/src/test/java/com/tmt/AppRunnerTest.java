package com.tmt;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import com.swak.cache.redis.RedisLocalCache;
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
	private RedisLocalCache localCache;

	@Test
	public void localCacheTest() {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		MultiThreadTest.run(() -> {
			while (true) {
				try {
					if (localCache != null) {
						localCache.putObject("name", "ifeng");
					}
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		}, 3, "测试存储数据");
		countDownLatch.countDown();
	}
}