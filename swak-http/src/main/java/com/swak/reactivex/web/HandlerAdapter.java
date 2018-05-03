package com.swak.reactivex.web;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;
import com.swak.reactivex.web.result.HandlerResult;

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
	boolean supports(Handler handler);
	
	/**
	 * 处理handler
	 * 
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	HandlerResult handle(HttpServerRequest request, HttpServerResponse response, Handler handler);
}
