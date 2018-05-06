package com.swak.reactivex.handler;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;

import reactor.core.publisher.Mono;

public interface WebFilterChain {

	Mono<Void> filter(HttpServerRequest request, HttpServerResponse response);
}
