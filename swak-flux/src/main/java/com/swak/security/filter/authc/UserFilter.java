package com.swak.security.filter.authc;

import org.springframework.util.StringUtils;

import com.swak.exception.ErrorCode;
import com.swak.reactivex.transport.http.Subject;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.reactivex.web.Result;
import com.swak.security.SecurityUtils;
import com.swak.security.filter.AccessControllerFilter;

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
		Subject subject = SecurityUtils.getSubject(request);
		return Mono.just(subject.getPrincipal() != null);
	}

	@Override
	protected Mono<Boolean> onAccessDenied(HttpServerRequest request, HttpServerResponse response) {
		Subject subject = SecurityUtils.getSubject(request);
		ErrorCode code = ErrorCode.NO_USER;
		if (StringUtils.hasText(subject.getReason())) {
			try {
				code = code.clone();
			} catch (CloneNotSupportedException e) {}
			code.setReason(subject.getReason());
		}
		response.json().status(HttpResponseStatus.OK).buffer(Result.error(code).toJson());
		return Mono.just(false);
	}
}