package com.swak.flux.security.filter.authc;

import com.swak.flux.security.filter.AccessControllerFilter;
import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.transport.http.server.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * 登录相关的操作
 * @author lifeng
 */
public abstract class AuthenticationFilter extends AccessControllerFilter {

	/**
	 * 都不需要访问登录请求
	 */
	@Override
	protected Mono<Boolean> isAccessAllowed(HttpServerRequest request,
			HttpServerResponse response, Object mappedValue) {
		return Mono.just(false);
	}
}