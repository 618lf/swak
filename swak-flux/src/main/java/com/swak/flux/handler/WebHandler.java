package com.swak.flux.handler;

import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.transport.http.server.HttpServerResponse;

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
