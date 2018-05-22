package com.swak.common.http.reactor;

import org.asynchttpclient.AsyncHttpClient;

/**
 * 响应式的 http 客户端
 * @author lifeng
 */
public class DefaultReactorHttpClient implements ReactorHttpClient{

	private final AsyncHttpClient httpClient;
	DefaultReactorHttpClient(AsyncHttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
}
