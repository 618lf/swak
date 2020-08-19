package com.swak.redis.pubsub;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.swak.SafeEncoder;
import com.swak.redis.MessageListener;
import com.swak.redis.RedisTest;

/**
 * 测试订阅发布
 * 
 * @author lifeng
 * @date 2020年8月19日 下午10:37:24
 */
public class TestPubSub extends RedisTest {

	@Test
	public void test() throws InterruptedException {
		CountDownLatch countDownLatch1 = new CountDownLatch(1);
		CountDownLatch countDownLatch2 = new CountDownLatch(1);
		MessageListener listener = (channel, message) -> {
			System.out.println("1. 收到消息" + channel);
			countDownLatch1.countDown();
		};
		redisService.event().subscribe(listener, SafeEncoder.encode("test-topic1"));
		
		// 等待结束
		countDownLatch1.await();
		
		// 取消订阅
		redisService.event().unSubscribe(listener, SafeEncoder.encode("test-topic1"));
		
		countDownLatch2.await();
	}
}
