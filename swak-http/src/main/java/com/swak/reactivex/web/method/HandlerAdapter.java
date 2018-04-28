package com.swak.reactivex.web.method;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;

/**
 * Handler 的处理器
 * 
 * @author lifeng
 */
public interface HandlerAdapter {

	/**
	 * 是否支持此处理器
	 * @param handlerMethod
	 * @return
	 */
	boolean supports(HandlerMethod handlerMethod);
	
	/**
	 * 处理handler
	 * 
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	HandlerResult handle(HttpServerRequest request, HttpServerResponse response, HandlerMethod handlerMethod);
}
