package com.swak.security.filter;

import com.swak.reactivex.handler.WebFilter;
import com.swak.reactivex.handler.WebFilterChain;
import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;

import io.reactivex.Observable;

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
	protected Observable<Void> executeChain(HttpServerRequest request, HttpServerResponse response,
			WebFilterChain chain) throws Exception {
		return chain.filter(request, response);
	}

	/**
	 * 执行 filter
	 */
	@Override
	public Observable<Void> filter(HttpServerRequest request, HttpServerResponse response, WebFilterChain chain) {
		try {
			boolean continueChain = preHandle(request, response);
			if (continueChain) {
				return executeChain(request, response, chain);
			}
		} catch (Exception e) {
			return Observable.error(e);
		}
		return Observable.empty();
	}
}
