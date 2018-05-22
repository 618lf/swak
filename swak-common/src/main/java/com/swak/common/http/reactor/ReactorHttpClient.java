package com.swak.common.http.reactor;

import org.asynchttpclient.AsyncHttpClient;

/**
 * use reactor 
 * @author lifeng
 *
 */
public interface ReactorHttpClient {
	
	/**
	 * create default http client
	 * @param httpClient
	 * @return
	 */
	default ReactorHttpClient create(AsyncHttpClient httpClient) {
		return new DefaultReactorHttpClient(httpClient);
	}
}