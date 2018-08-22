package com.swak.config.redis;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.Constants;
import com.swak.cache.CacheProperties;
import com.swak.cache.redis.RedisCacheManager;

/**
 * 会判断是否引入了缓存组件
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(RedisCacheManager.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableRedis", matchIfMissing = true)
public class CacheModuleAutoConfiguration {

	
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
	@Order(Ordered.HIGHEST_PRECEDENCE + 10)
	@ConditionalOnMissingBean(CacheConfigurationSupport.class)
	@EnableConfigurationProperties(CacheProperties.class)
	public static class CacheAutoConfiguration extends CacheConfigurationSupport {}
	
	/**
	 * Event bus
	 * @author lifeng
	 */
	@Configuration
	@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
	@Order(Ordered.HIGHEST_PRECEDENCE + 10)
	@ConditionalOnMissingBean(EventBusConfigurationSupport.class)
	@AutoConfigureAfter(CacheAutoConfiguration.class)
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableEventBus", matchIfMissing = true)
	public static class EventBusAutoConfiguration extends EventBusConfigurationSupport {
		public EventBusAutoConfiguration() {
			APP_LOGGER.debug("Loading Redis Event bus");
		}
	}
}
