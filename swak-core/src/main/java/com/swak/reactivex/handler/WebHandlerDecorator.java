package com.swak.reactivex.handler;

import com.swak.reactivex.transport.http.HttpServerRequest;
import com.swak.reactivex.transport.http.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * 代理实际的 WebHandler
 * @author lifeng
 */
public class WebHandlerDecorator implements WebHandler {

	private final WebHandler delegate;
	
	public WebHandlerDecorator(WebHandler delegate) {
		this.delegate = delegate;
	}
	
	public WebHandler getDelegate() {
		return this.delegate;
	}
	
	@Override
	public Mono<Void> handle(HttpServerRequest request, HttpServerResponse response) {
		return this.delegate.handle(request, response);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [delegate=" + this.delegate + "]";
	}
}