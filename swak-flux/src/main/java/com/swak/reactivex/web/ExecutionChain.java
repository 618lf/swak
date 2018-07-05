package com.swak.reactivex.web;

import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * handler + interceptor
 * 
 * @author lifeng
 */
public interface ExecutionChain {

	/**
	 * 获得handler 
	 * @return
	 */
	Handler getHandler();
	
	/**
	 * 前置处理器
	 * @param request
	 * @param response
	 * @return
	 */
	Mono<Boolean> applyPreHandle(HttpServerRequest request, HttpServerResponse response);
	
	/**
	 * 后置处理器
	 * @param request
	 * @param response
	 * @return
	 */
	Mono<Void> applyPostHandle(HttpServerRequest request, HttpServerResponse response);
}