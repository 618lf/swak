package com.swak.config.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import com.swak.Constants;
import com.swak.cache.CacheProperties;
import com.swak.cache.redis.RedisCacheManager;

/**
 * 会判断是否引入了缓存组件
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(RedisCacheManager.class)
@ConditionalOnMissingBean(CacheConfigurationSupport.class)
@EnableConfigurationProperties(CacheProperties.class)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableRedis", matchIfMissing = true)
public class CacheAutoConfiguration extends CacheConfigurationSupport
		implements ApplicationListener<ContextRefreshedEvent> {

	/**
	 * 系统初始化
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		this.init();
	}
}