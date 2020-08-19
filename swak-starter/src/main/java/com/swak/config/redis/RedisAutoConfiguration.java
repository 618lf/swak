package com.swak.config.redis;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.EhcacheBase;
import org.ehcache.core.EhcacheManager;
import org.ehcache.impl.config.executor.PooledExecutionServiceConfiguration;
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.swak.Constants;
import com.swak.cache.CacheManagers;
import com.swak.redis.RedisCacheManager;
import com.swak.redis.RedisConnectionFactory;
import com.swak.redis.RedisLocalCache;
import com.swak.redis.RedisService;
import com.swak.redis.policy.ExpiryPolicys;

/**
 * 会判断是否引入了缓存组件
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ RedisCacheManager.class })
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableRedis", matchIfMissing = true)
@Import({ LettuceAutoConfiguration.class })
public class RedisAutoConfiguration implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private RedisProperties cacheProperties;
	@Autowired
	private ResourceLoader resourceLoader;
	private RedisLocalCache redisLocalCache;

	/**
	 * Redis 服务
	 * 
	 * @return
	 */
	@Bean
	public RedisService redisService(RedisConnectionFactory<byte[], byte[]> connectionFactory) {
		return new RedisService(connectionFactory);
	}

	/**
	 * 配置 EhcacheManager
	 * 
	 * @return
	 */
	@Bean(destroyMethod = "close")
	public EhcacheManager ehcacheManager() {
		PooledExecutionServiceConfiguration serviceConfiguration = new PooledExecutionServiceConfiguration();
		serviceConfiguration.addDefaultPool("Daemon", 1, cacheProperties.getLocalPoolSize());
		EhcacheManager ehcacheManager = (EhcacheManager) CacheManagerBuilder.newCacheManagerBuilder()
				.using(serviceConfiguration).with(new CacheManagerPersistenceConfiguration(getCacheDiskPath()))
				.withDefaultDiskStoreThreadPool("Daemon").withDefaultEventListenersThreadPool("Daemon")
				.withDefaultWriteBehindThreadPool("Daemon").build(true);
		return ehcacheManager;
	}

	// 本地缓存存储目录
	private File getCacheDiskPath() {
		try {
			Resource resource = resourceLoader.getResource(cacheProperties.getLocalDiskPath());
			return resource != null ? resource.getFile() : null;
		} catch (IOException e) {
		}
		return new File(cacheProperties.getLocalDiskPath());
	}

	/**
	 * 配置 本地缓存 会订阅 Event
	 * 
	 * @return
	 */
	@Bean
	public RedisLocalCache redisLocalCache(RedisService redisService, EhcacheManager ehcacheManager) {
		CacheConfiguration<String, byte[]> configuration = CacheConfigurationBuilder
				.newCacheConfigurationBuilder(String.class, byte[].class,
						ResourcePoolsBuilder.heap(cacheProperties.getLocalHeadSize())
								.offheap(cacheProperties.getLocalOffHeadMB(), MemoryUnit.MB)
								.disk(cacheProperties.getLocalDiskMB(), MemoryUnit.MB, true))
				.withExpiry(ExpiryPolicys.fixedExpiryPolicy(Duration.ofSeconds(cacheProperties.getLocalLiveSeconds())))
				.build();
		EhcacheBase<String, byte[]> ehcache = (EhcacheBase<String, byte[]>) ehcacheManager
				.createCache(cacheProperties.getLocalName(), configuration);
		this.redisLocalCache = new RedisLocalCache(redisService, cacheProperties.getLocalName(), ehcache);
		return this.redisLocalCache;
	}

	/**
	 * 配置redis 缓存管理，并设置二级缓存
	 * 
	 * @return
	 */
	@Bean
	public RedisCacheManager redisCacheManager(RedisService redisService, RedisLocalCache localCache) {
		RedisCacheManager redisCacheManager = new RedisCacheManager(redisService, localCache);
		CacheManagers.setCacheManager(redisCacheManager);
		return redisCacheManager;
	}

	/**
	 * 系统初始化
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (this.redisLocalCache != null) {
			this.redisLocalCache.start();
		}
	}
}
