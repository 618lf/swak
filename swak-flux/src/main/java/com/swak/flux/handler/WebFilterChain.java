package com.swak.flux.handler;

import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.transport.http.server.HttpServerResponse;

import reactor.core.publisher.Mono;

public interface WebFilterChain {

	/**
	 * 执行后续的filter
	 * @param request
	 * @param response
	 * @return
	 */
	Mono<Void> filter(HttpServerRequest request, HttpServerResponse response);
}
