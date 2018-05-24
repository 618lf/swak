package com.swak.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import com.swak.common.cache.CacheManagers;
import com.swak.common.cache.CacheProperties;
import com.swak.common.cache.redis.RedisCacheManager;
import com.swak.common.cache.redis.RedisLocalCache;
import com.swak.common.cache.redis.RedisUtils;
import com.swak.common.cache.redis.factory.RedisClientDecorator;
import com.swak.common.cache.redis.factory.RedisConnectionPoolFactory;
import com.swak.common.utils.Lists;
import com.swak.reactivex.server.HttpServerProperties;
import com.swak.reactivex.server.TransportMode;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;

/**
 * 缓存配置
 * @author lifeng
 */
public class CacheConfigurationSupport {

	@Autowired
	private CacheProperties cacheProperties;
	@Autowired
	private HttpServerProperties serverProperties;
	
	/**
	 * 可以配置 event loop 等相关
	 * @return
	 */
	@Bean(destroyMethod = "shutdown")
    public ClientResources clientResources() {
		// 设置属性 -- lettuce 会根据属性决定启动什么类型 event loop
		System.setProperty("io.lettuce.core.epoll", "false");
		System.setProperty("io.lettuce.core.kqueue", "false");
		if (serverProperties.getMode() == TransportMode.EPOLL) {
			System.setProperty("io.lettuce.core.epoll", "true");
		}
        return DefaultClientResources.create();
    }
	
	/**
	 * 配置 RedisClient
	 * @param clientResources
	 * @return
	 */
	@Bean(destroyMethod = "shutdown")
    public RedisClient redisClient(ClientResources clientResources) {
		List<RedisURI> nodes = this.nodes();
        return RedisClient.create(clientResources, nodes.get(0));
    }
	
	/**
	 * 创建 PoolFactory
	 * @param cachePoolConfig
	 * @param client
	 * @return
	 */
	@Bean
	public RedisConnectionPoolFactory cachePoolFactory(RedisClient client) {
		RedisClientDecorator decorator = new RedisClientDecorator(client);
		RedisConnectionPoolFactory cachePoolFactory = new RedisConnectionPoolFactory(decorator);
		RedisUtils.setRedisConnectionFactory(cachePoolFactory);
		return cachePoolFactory;
	}
	
	/**
	 * 配置 本地缓存
	 * 会订阅 Event
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public RedisLocalCache redisLocalCache() {
		 Configuration configuration = new Configuration();
		 CacheConfiguration cacheConfiguration = new CacheConfiguration();
		 cacheConfiguration.setName(cacheProperties.getLocalName());
		 cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
	     cacheConfiguration.setMaxEntriesLocalHeap(cacheProperties.getLocalElements());
	     cacheConfiguration.setMaxEntriesLocalDisk(0);
	     cacheConfiguration.setTimeToIdleSeconds(0);
	     cacheConfiguration.setTimeToLiveSeconds(cacheProperties.getLocalLiveSeconds());
	     cacheConfiguration.setEternal(false);
		 configuration.addCache(cacheConfiguration);
		 CacheManager cacheManager = CacheManager.newInstance(configuration);
		 Ehcache ehcache = cacheManager.getCache(cacheProperties.getLocalName());
		 return new RedisLocalCache(cacheManager, ehcache);
	}
	
	/**
	 * 配置redis 缓存管理，并设置二级缓存
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public RedisCacheManager redisCacheManager(RedisLocalCache localCache) {
		CacheManagers.setCacheManager(new RedisCacheManager(localCache));
		return CacheManagers.manager();
	}
	
	private List<RedisURI> nodes() {
		List<RedisURI> nodes = Lists.newArrayList();
		String hosts = cacheProperties.getHosts().replaceAll("\\s", "");
		String[] hostPorts = hosts.split(",");
		for(String hostPort : hostPorts) {
			String[] hostPortArr = hostPort.split(":");
			RedisURI node = RedisURI.create(hostPortArr[0], Integer.parseInt(hostPortArr[1]));
			node.setPassword(cacheProperties.getPassword());
			nodes.add(node);
		}
		return nodes;
	}
}