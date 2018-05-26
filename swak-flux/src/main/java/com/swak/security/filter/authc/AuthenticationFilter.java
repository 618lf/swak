package com.swak.security.filter.authc;

import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.reactivex.web.annotation.RequestMethod;
import com.swak.security.filter.AccessControllerFilter;

/**
 * 登录相关的操作
 * @author lifeng
 */
public abstract class AuthenticationFilter extends AccessControllerFilter {

	/**
	 * 都不需要访问登录请求
	 */
	@Override
	protected boolean isAccessAllowed(HttpServerRequest request,
			HttpServerResponse response, Object mappedValue) {
		return false;
	}
	
	/**
	 * 是 post 提交
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	protected boolean isLoginSubmission(HttpServerRequest request, HttpServerResponse response) {
		return request.getRequestMethod().name().equalsIgnoreCase(RequestMethod.POST.name());
	}
}