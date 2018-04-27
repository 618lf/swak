package com.swak.reactivex.web;

import com.swak.http.HttpServletRequest;
import com.swak.mvc.method.HandlerMethod;

import io.reactivex.Observable;

/**
 * 获取实际的 handler
 * @author lifeng
 */
public interface HandlerMapping {

	/**
	 * 获取对应的 -- HandlerMethod
	 * @param request
	 * @return
	 */
	Observable<HandlerMethod> getHandler(HttpServletRequest request);
}