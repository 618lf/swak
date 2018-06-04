package com.swak.reactivex.handler;

import com.swak.reactivex.transport.http.HttpServerRequest;
import com.swak.reactivex.transport.http.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * 具体的 http 服务处理
 * @author lifeng
 */
public interface WebHandler {

	/**
	 * 处理请求返回可订阅对象
	 * @param request
	 * @param response
	 * @return
	 */
	Mono<Void> handle(HttpServerRequest request, HttpServerResponse response);
}
