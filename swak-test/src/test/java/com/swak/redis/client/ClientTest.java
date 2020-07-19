package com.swak.redis.client;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.operations.AsyncOperations;
import com.swak.cache.redis.operations.SyncOperations;
import com.swak.redis.RedisTest;

/**
 * 测试客户端获取数据的方式
 * 
 * @author lifeng
 * @date 2020年7月19日 下午12:34:38
 */
public class ClientTest extends RedisTest {

	@Test
	public void test() throws InterruptedException {

		// 同步处理
		SyncOperations.set("1", SafeEncoder.encode("1"));

		// 异步处理
		AsyncOperations.set("1", SafeEncoder.encode("1"));

		// 等待结束
		new CountDownLatch(1).await();
	}
}
