package com.swak.reactivex.web.method;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;

import io.reactivex.Observable;

/**
 * 处理结果
 * @author lifeng
 */
public interface HandlerResultHandler {

	/**
	 * 是否支持
	 * @return
	 */
	boolean supports(HandlerResult result);
	
	/**
	 * 处理结果
	 * @param request
	 * @param response
	 * @param result
	 */
	Observable<Void> handle(HttpServerRequest request, HttpServerResponse response, HandlerResult result);
}