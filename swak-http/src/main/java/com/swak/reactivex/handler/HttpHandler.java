package com.swak.reactivex.handler;

import com.swak.reactivex.server.HttpServerOperations;

import reactor.core.publisher.Mono;

/**
 * 处理 http 请求
 * @author lifeng
 */
public interface HttpHandler {

	/**
	 * 处理请求返回可订阅对象
	 * @param request
	 * @param response
	 * @return
	 */
	Mono<Void> apply(HttpServerOperations httpServerOptions);
}