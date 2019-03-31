package com.swak.flux.security.filter.authc;

import com.swak.flux.security.filter.PathMatchingFilter;
import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.transport.server.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * 匿名用户
 * @author lifeng
 */
public class AnonymousFilter extends PathMatchingFilter {

	@Override
	protected Mono<Boolean> onPreHandle(HttpServerRequest request, HttpServerResponse response, Object mappedValue) {
		return Mono.just(true);
	}
}
