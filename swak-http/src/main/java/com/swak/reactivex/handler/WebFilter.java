package com.swak.reactivex.handler;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;

import reactor.core.publisher.Mono;

public interface WebFilter {

	/**
	 * 处理请求返回可订阅对象
	 * @param request
	 * @param response
	 * @return
	 */
	Mono<Void> filter(HttpServerRequest request, HttpServerResponse response, WebFilterChain chain);
}