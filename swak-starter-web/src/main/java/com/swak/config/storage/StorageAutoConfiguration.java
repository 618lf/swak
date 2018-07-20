package com.swak.config.storage;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.Constants;
import com.swak.storage.StorageProperties;
import com.swak.storage.Storager;

/**
 * 存储组件
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(Storager.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@ConditionalOnMissingBean(StorageConfigurationSupport.class)
@EnableConfigurationProperties(StorageProperties.class)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableStorage", matchIfMissing = true)
public class StorageAutoConfiguration extends StorageConfigurationSupport{}
