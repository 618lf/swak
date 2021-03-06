package com.swak.flux.security.filter.authc;

import com.swak.exception.ErrorCode;
import com.swak.flux.security.exception.AuthenticationException;
import com.swak.flux.transport.Subject;
import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.transport.server.HttpServerResponse;
import com.swak.flux.web.Result;

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
		boolean authenticated = request.getSubject().isAuthenticated();
		if (authenticated) {
			response.json().ok().buffer(Result.success().toJson());
			return Mono.just(false);
		}
		
		// 不用分 get 或 post， 统一走登录流程即可
		return executeLogin(request, response);
	}

	/**
	 * 登录逻辑 -- 异常不外露
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected Mono<Boolean> executeLogin(HttpServerRequest request, HttpServerResponse response) {
		Subject subject = request.getSubject();
		return subject.login(request, response).doOnSuccess(v -> {
			onLoginSuccess(subject, request, response);
		}).onErrorResume(e->{
			if (e instanceof AuthenticationException) {
				this.onLoginFailure((AuthenticationException)e, request, response);
				return Mono.just(false);
			}
			// 其他的错误不处理
			return Mono.error(e);
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
		response.json().ok().buffer(Result.success().toJson());
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
		ErrorCode code = ((AuthenticationException)e).getErrorCode();
		response.json().ok().buffer(Result.error(code).toJson());
	}
}