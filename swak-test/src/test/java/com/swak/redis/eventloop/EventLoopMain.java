package com.swak.redis.eventloop;

import java.util.concurrent.TimeUnit;

import com.swak.cache.AsyncCache;
import com.swak.cache.Cache;
import com.swak.cache.CacheManager;
import com.swak.cache.redis.RedisCacheManager;
import com.swak.cache.redis.RedisUtils;
import com.swak.cache.redis.factory.RedisClientDecorator;
import com.swak.cache.redis.factory.RedisConnectionPoolFactory;
import com.swak.lettuce.resource.SharedEventLoopGroupProvider;
import com.swak.lettuce.resource.SharedNettyCustomizer;
import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.transport.TransportMode;
import com.swak.reactivex.transport.resources.LoopResources;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.metrics.CommandLatencyCollector;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.resource.EventLoopGroupProvider;
import io.netty.channel.EventLoopGroup;

/**
 * AbstractRedisClient
 * 
 * @author lifeng
 */
public class EventLoopMain {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		
		System.setProperty("io.lettuce.core.epoll", "false");
		System.setProperty("io.lettuce.core.kqueue", "false");

		// 自定义了event loop 线程
		CommandLatencyCollector commandLatencyCollector = CommandLatencyCollector.disabled();
		LoopResources loopResources = Contexts.createEventLoopResources(TransportMode.NIO, 1, -1,
				"Lettuce.me.", false, 2, TimeUnit.SECONDS);
		EventLoopGroup eventLoopGroup = loopResources.onClient();
		EventLoopGroupProvider eventLoopGroupProvider = new SharedEventLoopGroupProvider(eventLoopGroup,
				loopResources.workCount());
		ClientResources clientResources = DefaultClientResources.builder()
				.commandLatencyCollector(commandLatencyCollector).eventLoopGroupProvider(eventLoopGroupProvider)
				.eventExecutorGroup(eventLoopGroup).nettyCustomizer(new SharedNettyCustomizer()).build();

		RedisURI node = RedisURI.create("127.0.0.1", Integer.parseInt("6379"));
		node.setPassword("12345678....");
		RedisClient redisClient = RedisClient.create(clientResources, node);
		RedisClientDecorator decorator = new RedisClientDecorator(redisClient);
		RedisConnectionPoolFactory cachePoolFactory = new RedisConnectionPoolFactory(decorator);
		RedisUtils.setRedisConnectionFactory(cachePoolFactory);

		CacheManager cacheManager = new RedisCacheManager(null);
		Cache<String> _qrcodeCache = cacheManager.getCache("test", 60 * 5, false);
		AsyncCache<String> qrcodeCache = _qrcodeCache.async();
		qrcodeCache.getString("1").thenAccept(res -> {
			try {
				Thread.sleep(5002);
			} catch (InterruptedException e) {
			}
			System.out.println("读取数据结束:" + Thread.currentThread().getName());
		});
	}
}