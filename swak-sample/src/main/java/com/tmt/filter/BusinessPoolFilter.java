package com.tmt.filter;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.swak.reactivex.handler.WebFilter;
import com.swak.reactivex.handler.WebFilterChain;
import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 业务线程池
 * @author lifeng
 */
public class BusinessPoolFilter implements WebFilter {
	
	Executor executor = new ThreadPoolExecutor(100, 200,
			60 * 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	
	@Override
	public Mono<Void> filter(HttpServerRequest request, HttpServerResponse response, WebFilterChain chain) {
		Mono<Void> filters = chain.filter(request, response);
		return filters.subscribeOn(Schedulers.fromExecutor(executor));
	}
}