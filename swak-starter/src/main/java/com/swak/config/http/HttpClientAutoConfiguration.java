package com.swak.config.http;

import org.asynchttpclient.AsyncHttpClientConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.http.HttpClientProperties;
import com.swak.http.HttpService;

/**
 * HttpClient 服务配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ HttpService.class, AsyncHttpClientConfig.class })
@ConditionalOnMissingBean(HttpClientConfigurationSupport.class)
@EnableConfigurationProperties(HttpClientProperties.class)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableHttpClient", matchIfMissing = true)
public class HttpClientAutoConfiguration extends HttpClientConfigurationSupport {}