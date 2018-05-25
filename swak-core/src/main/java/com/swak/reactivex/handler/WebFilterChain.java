package com.swak.reactivex.handler;

import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;

import reactor.core.publisher.Mono;

public interface WebFilterChain {

	Mono<Void> filter(HttpServerRequest request, HttpServerResponse response);
}
