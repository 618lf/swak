package com.swak.security.filter.authc;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.mvc.annotation.RequestMethod;
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
	protected boolean isAccessAllowed(HttpServletRequest request,
			HttpServletResponse response, Object mappedValue) throws Exception {
		return false;
	}
	
	/**
	 * 是 post 提交
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	protected boolean isLoginSubmission(HttpServletRequest request, HttpServletResponse	response) {
		return request.getRequestMethod().equalsIgnoreCase(RequestMethod.POST.name());
	}
}