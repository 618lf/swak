package com.swak.security.filter;

import com.swak.reactivex.transport.http.HttpServerRequest;
import com.swak.reactivex.transport.http.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * 访问控制
 * @author lifeng
 */
public abstract class AccessControllerFilter extends PathMatchingFilter{

	/**
	 * 是否有权访问这个请求
	 * @param request
	 * @param response
	 * @param mappedValue
	 * @return
	 * @throws Exception
	 */
	protected abstract Mono<Boolean> isAccessAllowed(HttpServerRequest request, HttpServerResponse response, Object mappedValue);

	/**
	 * 如果没权访问这个请求，需要做什么动作
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected abstract Mono<Boolean> onAccessDenied(HttpServerRequest request, HttpServerResponse response);

	/**
	 * 将 preHandle 分为两步来执行
	 */
	public Mono<Boolean> onPreHandle(HttpServerRequest request, HttpServerResponse response, Object mappedValue) {
		return isAccessAllowed(request, response, mappedValue).flatMap((allowed) ->{
			if (!allowed) {
				return onAccessDenied(request, response);
			}
			return Mono.just(allowed);
		});
	}
}