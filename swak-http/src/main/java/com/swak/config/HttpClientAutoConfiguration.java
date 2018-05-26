package com.swak.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.swak.http.HttpClientProperties;

/**
 * HttpClient 服务配置
 * @author lifeng
 */
@Configuration
@ConditionalOnMissingBean(HttpClientConfigurationSupport.class)
@EnableConfigurationProperties(HttpClientProperties.class)
public class HttpClientAutoConfiguration extends HttpClientConfigurationSupport{}