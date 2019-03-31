package com.swak.flux.security.filter.authz;

import com.swak.exception.ErrorCode;
import com.swak.flux.security.filter.AccessControllerFilter;
import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.transport.http.server.HttpServerResponse;
import com.swak.flux.web.Result;

import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.core.publisher.Mono;

/**
 * 权限验证器
 * 
 * @author lifeng
 */
public abstract class AuthorizationFilter extends AccessControllerFilter {

	/**
	 * 权限验证
	 */
	protected Mono<Boolean> onAccessDenied(HttpServerRequest request, HttpServerResponse response) {
		response.json().status(HttpResponseStatus.OK).buffer(Result.error(ErrorCode.ACCESS_DENIED).toJson());
		return Mono.just(false);
	}
}