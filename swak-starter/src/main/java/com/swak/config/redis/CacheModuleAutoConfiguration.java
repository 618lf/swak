package com.swak.config.redis;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.cache.CacheProperties;
import com.swak.cache.redis.RedisCacheManager;
import com.swak.config.flux.SecurityAutoConfiguration;

/**
 * 会判断是否引入了缓存组件
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(RedisCacheManager.class)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableRedis", matchIfMissing = true)
@AutoConfigureBefore(SecurityAutoConfiguration.class)
public class CacheModuleAutoConfiguration {

	@Configuration
	@ConditionalOnMissingBean(CacheConfigurationSupport.class)
	@EnableConfigurationProperties(CacheProperties.class)
	public static class CacheAutoConfiguration extends CacheConfigurationSupport {}
	
	@Configuration
	@ConditionalOnMissingBean(EventBusConfigurationSupport.class)
	@AutoConfigureAfter(CacheAutoConfiguration.class)
	@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableEventBus", matchIfMissing = false)
	public static class EventBusAutoConfiguration extends EventBusConfigurationSupport {
		public EventBusAutoConfiguration() {
			APP_LOGGER.debug("Loading Redis Event bus");
		}
	}
}