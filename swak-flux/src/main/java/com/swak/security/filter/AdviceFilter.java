package com.swak.security.filter;

import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.reactivex.handler.WebFilter;
import com.swak.reactivex.handler.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 * 基本的 filter
 * 
 * @author lifeng
 */
public class AdviceFilter implements WebFilter {

	/**
	 * 执行前
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected boolean preHandle(HttpServerRequest request, HttpServerResponse response) {
		return true;
	}

	/**
	 * 执行后面的过滤器
	 * 
	 * @param request
	 * @param response
	 * @param chain
	 * @throws Exception
	 */
	protected Mono<Void> executeChain(HttpServerRequest request, HttpServerResponse response,
			WebFilterChain chain) throws Exception {
		return chain.filter(request, response);
	}

	/**
	 * 执行 filter
	 */
	@Override
	public Mono<Void> filter(HttpServerRequest request, HttpServerResponse response, WebFilterChain chain) {
		try {
			boolean continueChain = preHandle(request, response);
			if (continueChain) {
				return executeChain(request, response, chain);
			}
		} catch (Exception e) {
			return Mono.error(e);
		}
		return Mono.empty();
	}

	/**
	 * 这个不需要支持顺序
	 */
	@Override
	public int getOrder() {
		return 1;
	}
}
