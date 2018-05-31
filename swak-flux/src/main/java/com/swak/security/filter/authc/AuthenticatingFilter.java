package com.swak.security.filter.authc;

import com.swak.exception.ErrorCode;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.reactivex.Subject;
import com.swak.reactivex.web.Result;
import com.swak.security.exception.AuthenticationException;
import com.swak.security.utils.SecurityUtils;

import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.core.publisher.Mono;

/**
 * 实际的登录操作
 * 
 * @author lifeng
 */
public class AuthenticatingFilter extends AuthenticationFilter {

	/**
	 * 进来的是登录的地址
	 */
	@Override
	protected Mono<Boolean> onAccessDenied(HttpServerRequest request,
			HttpServerResponse response) {
		
		// 已登录直接返回
		boolean authenticated = SecurityUtils.getSubject(request).isAuthenticated();
		if (authenticated) {
			response.json().status(HttpResponseStatus.OK).buffer(Result.success().toJson());
			return Mono.just(false);
		}
		
		// 如果是登录请求 post 提交的， 则走登录流程
		if (isLoginSubmission(request, response)) {
			return executeLogin(request, response).map(v->true);
		}
		
		// 如果是登录请求 get 提交的， 直接返回错误
		response.json().status(HttpResponseStatus.OK).buffer(Result.error(ErrorCode.OPERATE_FAILURE).toJson());
		return Mono.just(false);
	}

	/**
	 * 登录逻辑
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected Mono<Void> executeLogin(HttpServerRequest request, HttpServerResponse response) {
		Subject subject = SecurityUtils.getSubject(request);
		return subject.login(request, response).doOnSuccessOrError((v, t) -> {
			if (t != null && t instanceof AuthenticationException) {
				onLoginFailure((AuthenticationException)t, request, response);
			} else {
				onLoginSuccess(subject, request, response);
			}
		});
	}

	/**
	 * 登录成功则不需要继续执行
	 * 
	 * @param token
	 * @param subject
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected void onLoginSuccess(Subject subject, HttpServerRequest request, HttpServerResponse response)  {
		response.json().status(HttpResponseStatus.OK).buffer(Result.success().toJson());
	}

	/**
	 * 登录失败则继续执行 -- 一定会抛出异常
	 * Ajax 请求不会往下执行，请不要在 login post 控制器中重要的逻辑
	 * @param e
	 * @param request
	 * @param response
	 * @return
	 */
	protected void onLoginFailure(AuthenticationException e, HttpServerRequest request, HttpServerResponse response) {
		ErrorCode code = e.getErrorCode();
		response.json().status(HttpResponseStatus.OK).buffer(Result.error(code).toJson());
	}
}