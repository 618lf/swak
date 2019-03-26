package com.swak.config.redis;

import static com.swak.Application.APP_LOGGER;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.EhcacheBase;
import org.ehcache.core.EhcacheManager;
import org.ehcache.impl.config.executor.PooledExecutionServiceConfiguration;
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.swak.cache.CacheManagers;
import com.swak.cache.CacheProperties;
import com.swak.cache.redis.RedisCacheManager;
import com.swak.cache.redis.RedisLocalCache;
import com.swak.cache.redis.RedisUtils;
import com.swak.cache.redis.factory.RedisClientDecorator;
import com.swak.cache.redis.factory.RedisConnectionPoolFactory;
import com.swak.cache.redis.policy.ExpiryPolicys;
import com.swak.reactivex.transport.TransportMode;
import com.swak.reactivex.transport.resources.EventLoops;
import com.swak.utils.Lists;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.metrics.CommandLatencyCollector;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;

/**
 * 缓存配置
 * 
 * @author lifeng
 */
public class CacheConfigurationSupport {

	@Autowired
	private CacheProperties cacheProperties;
	@Autowired
	private ResourceLoader resourceLoader;

	public CacheConfigurationSupport() {
		APP_LOGGER.debug("Loading Redis Cache");
	}

	/**
	 * 可以配置 event loop 等相关
	 * 
	 * @return
	 */
	@Bean(destroyMethod = "shutdown")
	public ClientResources clientResources(ObjectProvider<CommandLatencyCollector> commandLatencyCollectorProvider) {
		System.setProperty("io.lettuce.core.epoll", "false");
		System.setProperty("io.lettuce.core.kqueue", "false");
		if (cacheProperties.getMode() == TransportMode.EPOLL) {
			System.setProperty("io.lettuce.core.epoll", "true");
		}

		// 如果没有配置则不启用
		CommandLatencyCollector commandLatencyCollector = commandLatencyCollectorProvider.getIfAvailable();
		if (commandLatencyCollector == null) {
			commandLatencyCollector = CommandLatencyCollector.disabled();
		}
		ClientResources clientResources = DefaultClientResources.builder()
				.commandLatencyCollector(commandLatencyCollector).build();
		EventLoops.register("Lettuce", clientResources.eventExecutorGroup());
		return clientResources;
	}

	/**
	 * 配置 RedisClient
	 * 
	 * @param clientResources
	 * @return
	 */
	@Bean
	public RedisClientDecorator redisClient(ClientResources clientResources) {
		List<RedisURI> nodes = this.nodes();
		if (nodes.size() == 1) {
			return new RedisClientDecorator(RedisClient.create(clientResources, nodes.get(0)));
		}
		return new RedisClientDecorator(RedisClusterClient.create(clientResources, nodes));
	}

	/**
	 * 创建 PoolFactory
	 * 
	 * @param cachePoolConfig
	 * @param client
	 * @return
	 */
	@Bean
	public RedisConnectionPoolFactory cachePoolFactory(RedisClientDecorator decorator) {
		RedisConnectionPoolFactory cachePoolFactory = new RedisConnectionPoolFactory(decorator);
		RedisUtils.setRedisConnectionFactory(cachePoolFactory);
		return cachePoolFactory;
	}

	/**
	 * 配置 EhcacheManager
	 * 
	 * @return
	 */
	@Bean(destroyMethod = "close")
	@ConditionalOnMissingBean
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
	@ConditionalOnMissingBean
	public RedisLocalCache redisLocalCache(EhcacheManager ehcacheManager) {
		CacheConfiguration<String, byte[]> configuration = CacheConfigurationBuilder
				.newCacheConfigurationBuilder(String.class, byte[].class,
						ResourcePoolsBuilder.heap(cacheProperties.getLocalHeadSize())
								.offheap(cacheProperties.getLocalOffHeadMB(), MemoryUnit.MB)
								.disk(cacheProperties.getLocalDiskMB(), MemoryUnit.MB, true))
				.withExpiry(ExpiryPolicys.fixedExpiryPolicy(Duration.ofSeconds(cacheProperties.getLocalLiveSeconds())))
				.build();
		EhcacheBase<String, byte[]> ehcache = (EhcacheBase<String, byte[]>) ehcacheManager
				.createCache(cacheProperties.getLocalName(), configuration);
		return new RedisLocalCache(cacheProperties.getLocalName(), ehcache);
	}

	/**
	 * 配置redis 缓存管理，并设置二级缓存
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public com.swak.cache.CacheManager redisCacheManager(RedisLocalCache localCache) {
		CacheManagers.setCacheManager(new RedisCacheManager(localCache));
		return CacheManagers.manager();
	}

	private List<RedisURI> nodes() {
		List<RedisURI> nodes = Lists.newArrayList();
		String hosts = cacheProperties.getHosts().replaceAll("\\s", "");
		String[] hostPorts = hosts.split(",");
		for (String hostPort : hostPorts) {
			String[] hostPortArr = hostPort.split(":");
			RedisURI node = RedisURI.create(hostPortArr[0], Integer.parseInt(hostPortArr[1]));
			node.setPassword(cacheProperties.getPassword());
			nodes.add(node);
		}
		return nodes;
	}
}