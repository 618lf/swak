package com.swak.flux.web;

import java.util.List;

import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.transport.server.HttpServerResponse;
import com.swak.flux.web.interceptor.HandlerInterceptor;

import reactor.core.publisher.Mono;

/**
 * handler + interceptor
 * 
 * @author lifeng
 */
public interface ExecutionChain {

	/**
	 * 获得 Handler 
	 * @return
	 */
	Handler getHandler();
	
	/**
	 * 获得 HandlerInterceptor
	 * @return
	 */
	List<HandlerInterceptor> getInterceptors();
	
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