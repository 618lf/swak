package com.swak.config;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.Constants;

/**
 * 启动数据库配置
 * @author lifeng
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableDataBase", matchIfMissing = true)
@ConditionalOnMissingBean(DataBaseConfigurationSupport.class)
public class DataBaseAutoConfiguration extends DataBaseConfigurationSupport{}