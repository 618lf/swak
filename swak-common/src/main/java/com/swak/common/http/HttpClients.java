package com.swak.common.http;

import org.asynchttpclient.AsyncHttpClient;

public class HttpClients {

	// 需要http client 才能使用
	private static AsyncHttpClient httpClient = null;
	public static void setAsyncHttpClient(AsyncHttpClient httpClient) {
		HttpClients.httpClient = httpClient;
	}
	public static AsyncHttpClient client() {
		return httpClient;
	}
}