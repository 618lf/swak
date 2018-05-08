package com.swak.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import com.swak.common.cache.CacheProperties;
import com.swak.common.cache.ehcache.EhCacheCacheManager;
import com.swak.common.cache.redis.RedisCacheManager;
import com.swak.common.cache.redis.factory.RedisClientDecorator;
import com.swak.common.cache.redis.factory.RedisConnectionPoolFactory;
import com.swak.common.utils.Lists;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;

/**
 * 缓存配置
 * @author lifeng
 */
public class CacheConfigurationSupport {

	@Autowired
	private CacheProperties cacheProperties;
	
	/**
	 * 可以配置 event loop 等相关
	 * @return
	 */
	@Bean(destroyMethod = "shutdown")
    public ClientResources clientResources() {
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
	public RedisConnectionPoolFactory cachePoolFactory(CacheProperties cacheProperties, RedisClient client) {
		RedisClientDecorator decorator = new RedisClientDecorator(client);
		RedisConnectionPoolFactory cachePoolFactory = new RedisConnectionPoolFactory(decorator, cacheProperties);
		return cachePoolFactory;
	}
	
	/**
	 * 配置 ehCache
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public EhCacheCacheManager ehCacheCacheManager() {
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
		 return new EhCacheCacheManager(CacheManager.newInstance(configuration));
	}
	
	/**
	 * 配置 ehCache
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public RedisCacheManager redisCacheManager() {
		 return new RedisCacheManager();
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