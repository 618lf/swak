package com.tmt.filter;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.core.Ordered;

import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.reactivex.handler.WebFilter;
import com.swak.reactivex.handler.WebFilterChain;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 业务线程池
 * 还是需要线程池来处理业务。
 * @author lifeng
 */
public class BusinessPoolFilter implements WebFilter, Ordered {
	
	Executor executor = new ThreadPoolExecutor(1024, 2000,
			60 * 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	
	@Override
	public Mono<Void> filter(HttpServerRequest request, HttpServerResponse response, WebFilterChain chain) {
		return chain.filter(request, response).subscribeOn(Schedulers.fromExecutor(executor));
	}

	/**
	 * 这个靠前面执行
	 */
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}