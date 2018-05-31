package com.swak.security.principal;

import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.reactivex.Subject;

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
	Mono<Void> createPrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response);

	/**
	 * 将身份失效
	 * 
	 * @param request
	 * @param response
	 */
	Mono<Void> invalidatePrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response);

	/**
	 * 获取身份
	 * 
	 * @param subject
	 * @param request
	 * @param response
	 */
	Mono<Subject> resolvePrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response);

	/**
	 * 将此 sessionId 对应的 身份失效
	 * 
	 * @param request
	 * @param response
	 */
	Mono<Void> invalidatePrincipal(String sessionId);
}
