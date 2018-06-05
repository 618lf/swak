package com.swak.security.filter.authc;

import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.security.filter.AccessControllerFilter;

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