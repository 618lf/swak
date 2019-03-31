package com.swak.flux.web;

import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.transport.http.server.HttpServerResponse;

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
	Object handle(HttpServerRequest request, HttpServerResponse response, Handler handler);
}
