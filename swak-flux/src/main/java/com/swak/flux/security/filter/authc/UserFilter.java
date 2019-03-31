package com.swak.flux.security.filter.authc;

import com.swak.exception.ErrorCode;
import com.swak.flux.security.filter.AccessControllerFilter;
import com.swak.flux.transport.http.Subject;
import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.transport.http.server.HttpServerResponse;
import com.swak.flux.web.Result;

import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.core.publisher.Mono;

/**
 * 用户访问权限, 有可能需要访问用户状态
 * @author lifeng
 */
public class UserFilter extends AccessControllerFilter {

	@Override
	protected Mono<Boolean> isAccessAllowed(HttpServerRequest request,
			HttpServerResponse response, Object mappedValue) {
		Subject subject = request.getSubject();
		return Mono.just(subject.getPrincipal() != null);
	}

	@Override
	protected Mono<Boolean> onAccessDenied(HttpServerRequest request, HttpServerResponse response) {
		ErrorCode code = ErrorCode.NO_USER;
		response.json().status(HttpResponseStatus.OK).buffer(Result.error(code).toJson());
		return Mono.just(false);
	}
}