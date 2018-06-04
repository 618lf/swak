package com.swak.reactivex.handler;

import com.swak.reactivex.transport.http.HttpServerRequest;
import com.swak.reactivex.transport.http.HttpServerResponse;

import reactor.core.publisher.Mono;

public interface WebFilterChain {

	Mono<Void> filter(HttpServerRequest request, HttpServerResponse response);
}
