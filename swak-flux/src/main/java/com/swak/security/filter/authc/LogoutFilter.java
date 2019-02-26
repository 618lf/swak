package com.swak.security.filter.authc;

import com.swak.reactivex.transport.http.Subject;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.reactivex.web.Result;
import com.swak.security.filter.AdviceFilter;

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