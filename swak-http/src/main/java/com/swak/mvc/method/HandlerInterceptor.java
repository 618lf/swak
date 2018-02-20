package com.swak.mvc.method;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

/**
 * 拦截器
 * 
 * @author lifeng
 *
 */
public interface HandlerInterceptor {
	default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		return true;
	}
	default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	}
	default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}