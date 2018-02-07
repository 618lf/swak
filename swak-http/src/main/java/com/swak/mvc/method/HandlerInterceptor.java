package com.swak.mvc.method;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

public interface HandlerInterceptor {

	boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

	void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception;

	void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception;
}
