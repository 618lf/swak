package com.swak.flux.security.filter;

import com.swak.flux.handler.WebFilter;
import com.swak.flux.handler.WebFilterChain;
import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.transport.http.server.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * 基本的 filter
 * 
 * @author lifeng
 */
public abstract class AdviceFilter implements WebFilter {

	/**
	 * 执行前
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected abstract Mono<Boolean> preHandle(HttpServerRequest request, HttpServerResponse response);

	/**
	 * 执行后面的过滤器
	 * 
	 * @param request
	 * @param response
	 * @param chain
	 * @throws Exception
	 */
	protected Mono<Void> executeChain(HttpServerRequest request, HttpServerResponse response,
			WebFilterChain chain) {
		return chain.filter(request, response);
	}

	/**
	 * 执行 filter
	 */
	@Override
	public Mono<Void> filter(HttpServerRequest request, HttpServerResponse response, WebFilterChain chain) {
		return preHandle(request, response).flatMap(continueChain ->{
			if (continueChain) {
				return executeChain(request, response, chain);
			}
			return Mono.empty();
		});
	}

	/**
	 * 这个不需要支持顺序
	 */
	@Override
	public int getOrder() {
		return 1;
	}
}
