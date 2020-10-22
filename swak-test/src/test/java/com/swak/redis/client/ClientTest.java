package com.swak.redis.client;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.swak.SafeEncoder;
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

		redisService.sync().set("1", SafeEncoder.encode("111"));
		System.out.println(SafeEncoder.encode(redisService.sync().get("1")));

		// 等待结束
		new CountDownLatch(1).await();
	}
	
	@Test
	public void script() throws InterruptedException {

		
		System.out.println(redisService.sync().ttl("11"));
		
		// 等待结束
		new CountDownLatch(1).await();
	}
}
