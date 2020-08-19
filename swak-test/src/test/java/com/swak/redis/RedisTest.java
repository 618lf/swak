package com.swak.redis;

import org.junit.Before;

import com.swak.redis.lettuce.LettuceClientDecorator;
import com.swak.redis.lettuce.LettuceConnectionFactory;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.DefaultClientResources;

/**
 * 锁测试
 * 
 * @author lifeng
 */
public class RedisTest {

	protected RedisService redisService;

	/**
	 * 初始化连接
	 */
	@Before
	public void init() {
		RedisURI node = RedisURI.create("127.0.0.1", Integer.parseInt("6379"));
		node.setPassword("12345678....");
		RedisClient redisClient = RedisClient.create(DefaultClientResources.create(), node);
		LettuceClientDecorator decorator = new LettuceClientDecorator(redisClient);
		LettuceConnectionFactory cachePoolFactory = new LettuceConnectionFactory(decorator);
		redisService = new RedisService(cachePoolFactory);
	}
}