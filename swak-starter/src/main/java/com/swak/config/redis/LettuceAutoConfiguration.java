package com.swak.config.redis;

import static com.swak.Application.APP_LOGGER;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.transport.TransportMode;
import com.swak.reactivex.transport.resources.LoopResources;
import com.swak.redis.RedisCacheManager;
import com.swak.redis.lettuce.LettuceClientDecorator;
import com.swak.redis.lettuce.LettuceConnectionFactory;
import com.swak.redis.lettuce.SharedEventLoopGroupProvider;
import com.swak.redis.lettuce.SharedNettyCustomizer;
import com.swak.utils.Lists;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.metrics.CommandLatencyCollector;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.resource.EventLoopGroupProvider;
import io.netty.channel.EventLoopGroup;

/**
 * 会判断是否引入了缓存组件
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ RedisCacheManager.class, ClientResources.class })
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableRedis", matchIfMissing = true)
public class LettuceAutoConfiguration {

	@Autowired
	private RedisProperties cacheProperties;

	public LettuceAutoConfiguration() {
		APP_LOGGER.debug("Loading Redis Cache");
	}

	/**
	 * 可以配置 event loop 等相关
	 * 
	 * @see 自定义eventloop之后需要自己管理停止
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
		LoopResources loopResources = Contexts.createEventLoopResources(cacheProperties.getMode(), 1, -1, "Lettuce.",
				true, 2, TimeUnit.SECONDS);
		EventLoopGroup eventLoopGroup = loopResources.onClient();
		EventLoopGroupProvider eventLoopGroupProvider = new SharedEventLoopGroupProvider(eventLoopGroup,
				loopResources.workCount());
		ClientResources clientResources = DefaultClientResources.builder()
				.commandLatencyCollector(commandLatencyCollector).eventLoopGroupProvider(eventLoopGroupProvider)
				.eventExecutorGroup(eventLoopGroup).nettyCustomizer(new SharedNettyCustomizer()).build();
		return clientResources;
	}

	/**
	 * 配置 RedisClient
	 * 
	 * @param clientResources
	 * @return
	 */
	@Bean
	public LettuceClientDecorator redisClient(ClientResources clientResources) {
		List<RedisURI> nodes = this.nodes();
		if (nodes.size() == 1) {
			return new LettuceClientDecorator(RedisClient.create(clientResources, nodes.get(0)));
		}
		return new LettuceClientDecorator(RedisClusterClient.create(clientResources, nodes));
	}

	/**
	 * 创建 PoolFactory
	 * 
	 * @param cachePoolConfig
	 * @param client
	 * @return
	 */
	@Bean
	public LettuceConnectionFactory cachePoolFactory(LettuceClientDecorator decorator) {
		LettuceConnectionFactory cachePoolFactory = new LettuceConnectionFactory(decorator);
		return cachePoolFactory;
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