package com.swak.reactivex.handler;

import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;

import reactor.core.publisher.Mono;

public interface WebFilterChain {

	Mono<Void> filter(HttpServerRequest request, HttpServerResponse response);
}
