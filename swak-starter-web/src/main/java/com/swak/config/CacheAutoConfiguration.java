package com.swak.config;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.Constants;
import com.swak.cache.CacheProperties;

/**
 * 缓存 服务配置
 * 
 * @author lifeng
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@ConditionalOnClass(CacheConfigurationSupport.class)
@ConditionalOnMissingBean(CacheConfigurationSupport.class)
@EnableConfigurationProperties(CacheProperties.class)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableRedis", matchIfMissing = true)
@Import({CacheModuleAutoConfiguration.class})
public class CacheAutoConfiguration {}