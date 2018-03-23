package com.swak.security.filter.authc;

import com.swak.http.ErrorCode;
import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.http.Result;
import com.swak.security.exception.AuthenticationException;
import com.swak.security.subjct.Subject;
import com.swak.security.utils.SecurityUtils;

import io.netty.handler.codec.http.HttpResponseStatus;

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
	protected boolean onAccessDenied(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		// 已登录直接返回
		boolean authenticated = SecurityUtils.getSubject().isAuthenticated();
		if (authenticated) {
			response.json().status(HttpResponseStatus.OK).out(Result.success().toJson());
			return false;
		}
		
		// 如果是登录请求 post 提交的， 则走登录流程
		if (isLoginSubmission(request, response)) {
			return executeLogin(request, response);
		}
		
		// 如果是登录请求 get 提交的， 直接返回错误
		response.json().status(HttpResponseStatus.OK).out(Result.error(ErrorCode.OPERATE_FAILURE).toJson());
		return false;
	}

	/**
	 * 登录逻辑
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected boolean executeLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			Subject subject = SecurityUtils.getSubject();
			subject.login(request, response);
			return onLoginSuccess(subject, request, response);
		} catch (AuthenticationException e) {
			return onLoginFailure(e, request, response);
		}
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
	protected boolean onLoginSuccess(Subject subject, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.json().status(HttpResponseStatus.OK).out(Result.success().toJson());
		return false;
	}

	/**
	 * 登录失败则继续执行 -- 一定会抛出异常
	 * Ajax 请求不会往下执行，请不要在 login post 控制器中重要的逻辑
	 * @param e
	 * @param request
	 * @param response
	 * @return
	 */
	protected boolean onLoginFailure(AuthenticationException e, HttpServletRequest request, HttpServletResponse response) {
		ErrorCode code = e.getErrorCode();
		response.json().status(HttpResponseStatus.OK).out(Result.error(code).toJson());
		return false;
	}
}