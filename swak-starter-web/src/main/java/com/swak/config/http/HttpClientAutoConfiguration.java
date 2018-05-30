package com.swak.config.http;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.Constants;
import com.swak.http.HttpClientProperties;
import com.swak.http.HttpClients;

/**
 * HttpClient 服务配置
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(HttpClients.class)
@ConditionalOnMissingBean(HttpClientConfigurationSupport.class)
@EnableConfigurationProperties(HttpClientProperties.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 150)
@Order(Ordered.HIGHEST_PRECEDENCE + 150)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableHttpClient", matchIfMissing = true)
public class HttpClientAutoConfiguration extends HttpClientConfigurationSupport{}