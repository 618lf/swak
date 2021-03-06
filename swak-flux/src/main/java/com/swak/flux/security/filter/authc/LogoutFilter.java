package com.swak.flux.security.filter.authc;

import com.swak.flux.security.filter.AdviceFilter;
import com.swak.flux.transport.Subject;
import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.transport.server.HttpServerResponse;
import com.swak.flux.web.Result;

import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.core.publisher.Mono;

/**
 * 退出登录
 * @author lifeng
 */
public class LogoutFilter extends AdviceFilter {

	@Override
	protected Mono<Boolean> preHandle(HttpServerRequest request, HttpServerResponse response) {
		Subject subject = request.getSubject();
		return subject.logout(request, response).doOnSuccess(s ->{
			response.json().status(HttpResponseStatus.OK).buffer(Result.success().toJson());
		});
	}
}