package com.swak.flux.handler;

import java.util.function.BiFunction;

import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.transport.server.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * 处理 http 请求
 * @author lifeng
 */
public interface HttpHandler extends BiFunction<HttpServerRequest, HttpServerResponse, Mono<Void>>{

	/**
	 * 处理请求返回可订阅对象
	 * @param request
	 * @param response
	 * @return
	 */
	Mono<Void> apply(HttpServerRequest request, HttpServerResponse response);
}