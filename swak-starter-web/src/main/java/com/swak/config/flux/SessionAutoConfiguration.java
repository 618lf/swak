package com.swak.config.flux;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.Constants;

/**
 * session 支持
 * 
 * @author lifeng
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@ConditionalOnMissingBean(SessionConfigurationSupport.class)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableSession", matchIfMissing = true)
public class SessionAutoConfiguration extends SessionConfigurationSupport {}
