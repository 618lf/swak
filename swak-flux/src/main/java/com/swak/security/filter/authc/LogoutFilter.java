package com.swak.security.filter.authc;

import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.reactivex.Subject;
import com.swak.reactivex.web.Result;
import com.swak.security.filter.AdviceFilter;
import com.swak.security.utils.SecurityUtils;

import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.core.publisher.Mono;

/**
 * 退出登录
 * @author lifeng
 */
public class LogoutFilter extends AdviceFilter {

	@Override
	protected Mono<Boolean> preHandle(HttpServerRequest request, HttpServerResponse response) {
		Subject subject = SecurityUtils.getSubject(request);
		return subject.logout(request, response).doOnSuccess(s ->{
			response.json().status(HttpResponseStatus.OK).buffer(Result.success().toJson());
		});
	}
}