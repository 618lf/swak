package com.swak.flux.web.interceptor;

import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.transport.server.HttpServerResponse;
import com.swak.flux.web.ExecutionChain;

import reactor.core.publisher.Mono;

/**
 * 拦截器
 * 
 * @author lifeng
 *
 */
public interface HandlerInterceptor {
	
	/**
	 * 之前handler 之前
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	default Mono<Boolean> preHandle(HttpServerRequest request, HttpServerResponse response, ExecutionChain chain) {
		return chain.applyPreHandle(request, response);
	}

	/**
	 * 之前handler 之后
	 * @param request
	 * @param response
	 * @param handler
	 * @throws Exception
	 */
	default Mono<Void> postHandle(HttpServerRequest request, HttpServerResponse response, ExecutionChain chain) {
		return chain.applyPostHandle(request, response);
	}
}