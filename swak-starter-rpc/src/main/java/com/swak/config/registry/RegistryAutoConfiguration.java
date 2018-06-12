package com.swak.config.registry;

import static com.swak.Application.APP_LOGGER;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.Constants;
import com.swak.cache.CacheProperties;
import com.swak.reactivex.transport.TransportMode;
import com.swak.rpc.server.RpcServerProperties;
import com.swak.utils.Lists;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;

/**
 * 会判断是否引入了缓存组件
 * @author lifeng
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableRedis", matchIfMissing = true)
public class RegistryAutoConfiguration {
    
	@Autowired
	private CacheProperties cacheProperties;
	@Autowired
	private RpcServerProperties serverProperties;
	
	public RegistryAutoConfiguration() {
		APP_LOGGER.debug("Loading Redis Cache");
	}
	
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
