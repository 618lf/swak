package com.swak.test;

import org.junit.Before;

import com.swak.cache.redis.RedisUtils;
import com.swak.cache.redis.factory.RedisClientDecorator;
import com.swak.cache.redis.factory.RedisConnectionPoolFactory;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.DefaultClientResources;

/**
 * 锁测试
 * 
 * @author lifeng
 */
public class RedisTest {

	/**
	 * 初始化连接
	 */
	@Before
	public void init() {
		RedisURI node = RedisURI.create("127.0.0.1", Integer.parseInt("6379"));
		node.setPassword("12345678....");
		RedisClient redisClient = RedisClient.create(DefaultClientResources.create(), node);
		RedisClientDecorator decorator = new RedisClientDecorator(redisClient);
		RedisConnectionPoolFactory cachePoolFactory = new RedisConnectionPoolFactory(decorator);
		RedisUtils.setRedisConnectionFactory(cachePoolFactory);
	}
}