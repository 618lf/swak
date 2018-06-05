package com.swak.security.filter.authc;

import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.security.filter.PathMatchingFilter;

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
