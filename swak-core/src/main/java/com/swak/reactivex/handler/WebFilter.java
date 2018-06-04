package com.swak.reactivex.handler;

import com.swak.reactivex.transport.http.HttpServerRequest;
import com.swak.reactivex.transport.http.HttpServerResponse;

import reactor.core.publisher.Mono;

public interface WebFilter {
	
	

	/**
	 * filter 的顺序
	 * @return
	 */
	int getOrder();
	
	/**
	 * 处理请求返回可订阅对象
	 * @param request
	 * @param response
	 * @return
	 */
	Mono<Void> filter(HttpServerRequest request, HttpServerResponse response, WebFilterChain chain);
}