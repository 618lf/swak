package com.swak.security.filter;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

/**
 * 访问控制
 * @author lifeng
 */
public abstract class AccessControllerFilter extends PathMatchingFilter {

	/**
	 * 是否有权访问这个请求
	 * @param request
	 * @param response
	 * @param mappedValue
	 * @return
	 * @throws Exception
	 */
	protected abstract boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response, Object mappedValue) throws Exception;

	/**
	 * 如果没权访问这个请求，需要做什么动作
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected abstract boolean onAccessDenied(HttpServletRequest request, HttpServletResponse response) throws Exception;

	/**
	 * 将 preHandle 分为两步来执行
	 */
	public boolean onPreHandle(HttpServletRequest request, HttpServletResponse response, Object mappedValue) throws Exception {
		return isAccessAllowed(request, response, mappedValue) || onAccessDenied(request, response);
	}
}