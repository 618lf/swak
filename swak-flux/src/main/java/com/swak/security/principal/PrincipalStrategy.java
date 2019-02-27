package com.swak.security.principal;

import com.swak.reactivex.transport.http.Subject;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * 身份的存储策略
 * @author lifeng
 */
public interface PrincipalStrategy {

	/**
	 * 创建身份
	 * 
	 * @param session
	 * @param request
	 * @param response
	 */
	Mono<Subject> createPrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response);

	/**
	 * 将身份失效
	 * 
	 * @param request
	 * @param response
	 */
	Mono<Boolean> invalidatePrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response);

	/**
	 * 获取身份
	 * 
	 * @param subject
	 * @param request
	 * @param response
	 */
	Mono<Subject> resolvePrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response);
}
