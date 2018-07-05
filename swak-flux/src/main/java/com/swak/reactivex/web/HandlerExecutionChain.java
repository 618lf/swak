package com.swak.reactivex.web;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.ObjectUtils;

import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.reactivex.web.interceptor.HandlerInterceptor;
import com.swak.utils.Lists;

import reactor.core.publisher.Mono;

/**
 * 执行链
 * 
 * @author lifeng
 */
public class HandlerExecutionChain implements ExecutionChain {

	private final Handler handler;
	private List<HandlerInterceptor> interceptorList;
	private int index;

	public HandlerExecutionChain(Handler handler) {
		this.handler = handler;
	}

	@Override
	public Handler getHandler() {
		return handler;
	}

	public void addInterceptors(HandlerInterceptor... interceptors) {
		if (!ObjectUtils.isEmpty(interceptors)) {
			initInterceptorList().addAll(Arrays.asList(interceptors));
		}
	}

	private List<HandlerInterceptor> initInterceptorList() {
		if (this.interceptorList == null) {
			this.interceptorList = Lists.newArrayList();
		}
		return this.interceptorList;
	}
	
	@Override
	public Mono<Boolean> applyPreHandle(HttpServerRequest request, HttpServerResponse response) {
		if (this.index < this.interceptorList.size()) {
			return this.interceptorList.get(this.index++).preHandle(request, response, handler);
		}
		return Mono.just(true);
	}

	@Override
	public Mono<Void> applyPostHandle(HttpServerRequest request, HttpServerResponse response) {
		if (this.index < this.interceptorList.size()) {
			return this.interceptorList.get(this.index++).postHandle(request, response, handler);
		}
		return Mono.empty();
	}
}