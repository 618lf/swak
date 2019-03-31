package com.swak.flux.handler;

import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.transport.http.server.HttpServerResponse;

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