package com.swak.config.http;

import static com.swak.Application.APP_LOGGER;

import java.util.concurrent.atomic.AtomicLong;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig.Builder;
import org.springframework.context.annotation.Bean;

import com.swak.http.HttpClientProperties;
import com.swak.http.HttpClients;
import com.swak.reactivex.transport.TransportMode;
import com.swak.reactivex.transport.resources.EventLoopFactory;
import com.swak.reactivex.transport.resources.EventLoops;

import io.netty.channel.EventLoopGroup;

/**
 * 配置 http client
 * 
 * @author lifeng
 */
public class HttpClientConfigurationSupport {

	public HttpClientConfigurationSupport() {
		APP_LOGGER.debug("Loading Http client");
	}

	/**
	 * 连接的配置项
	 * 
	 * @return
	 */
	@Bean
	public AsyncHttpClientConfig httpClientConfig(HttpClientProperties httpClientProperties) {
		Builder builder = new DefaultAsyncHttpClientConfig.Builder();
		if (httpClientProperties.getMode() == TransportMode.EPOLL) {
			builder.setUseNativeTransport(true);
		}
		builder.setConnectTimeout(httpClientProperties.getConnectTimeout());
		builder.setReadTimeout(httpClientProperties.getReadTimeout());
		builder.setRequestTimeout(httpClientProperties.getRequestTimeout());
		builder.setHandshakeTimeout(httpClientProperties.getHandshakeTimeout());
		builder.setUserAgent(httpClientProperties.getUserAgent());
		builder.setThreadFactory(new EventLoopFactory(true, "AsyncHttp.", new AtomicLong()));
		return builder.build();
	}

	/**
	 * 连接 -- 线程安全的
	 * 
	 * @param config
	 * @return
	 */
	@Bean(destroyMethod = "close")
	public AsyncHttpClient asyncHttpClient(AsyncHttpClientConfig config) {
		DefaultAsyncHttpClient httpClient = new DefaultAsyncHttpClient(config);
		EventLoopGroup eventLoopGroup = httpClient.getEventLoopGroup();
		EventLoops.register("AsyncHttp", eventLoopGroup);
		HttpClients.setAsyncHttpClient(httpClient);
		return HttpClients.client();
	}
}