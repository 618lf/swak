package com.swak.config;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.Constants;
import com.swak.http.HttpClientProperties;

/**
 * HttpClient 服务配置
 * @author lifeng
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 200)
@Order(Ordered.HIGHEST_PRECEDENCE + 200)
@ConditionalOnMissingBean(HttpClientConfigurationSupport.class)
@EnableConfigurationProperties(HttpClientProperties.class)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableHttpClient", matchIfMissing = true)
public class HttpClientAutoConfiguration extends HttpClientConfigurationSupport{
}
