package com.swak.config.http;

import static com.swak.Application.APP_LOGGER;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import com.swak.http.HttpClientProperties;
import com.swak.http.HttpClients;
import com.swak.reactivex.transport.TransportMode;
import com.swak.reactivex.transport.http.server.HttpServerProperties;

/**
 * 配置 http client
 * @author lifeng
 */
@ConditionalOnClass(AsyncHttpClientConfig.class)
public class HttpClientConfigurationSupport {
	
	@Autowired
	private HttpClientProperties httpClientProperties;
	
	@Autowired
	private HttpServerProperties serverProperties;
	
	public HttpClientConfigurationSupport() {
		APP_LOGGER.debug("Loading Http client");
	}

	/**
	 * 连接的配置项
	 * @return
	 */
	@Bean
	public AsyncHttpClientConfig httpClientConfig() {
		Builder builder = new DefaultAsyncHttpClientConfig.Builder();
		if (serverProperties.getMode() == TransportMode.EPOLL) {
			builder.setUseNativeTransport(true);
		}
		builder.setConnectTimeout(httpClientProperties.getConnectTimeout());
		builder.setReadTimeout(httpClientProperties.getReadTimeout());
		builder.setRequestTimeout(httpClientProperties.getRequestTimeout());
		builder.setHandshakeTimeout(httpClientProperties.getHandshakeTimeout());
		builder.setUserAgent(httpClientProperties.getUserAgent());
		return builder.build();
	}
	
	/**
	 * 连接 -- 线程安全的
	 * @param config
	 * @return
	 */
	@Bean(destroyMethod="close")
	public AsyncHttpClient asyncHttpClient(AsyncHttpClientConfig config) {
		HttpClients.setAsyncHttpClient(new DefaultAsyncHttpClient(config));
		return HttpClients.client();
	}
}