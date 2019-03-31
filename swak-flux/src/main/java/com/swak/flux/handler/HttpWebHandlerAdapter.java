package com.swak.flux.handler;

import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.transport.http.server.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * httpHandler -> webHandler
 * @author lifeng
 */
public class HttpWebHandlerAdapter extends WebHandlerDecorator implements HttpHandler {

	public HttpWebHandlerAdapter(WebHandler delegate) {
		super(delegate);
	}

	/**
	 * 处理请求
	 */
	@Override
	public Mono<Void> apply(HttpServerRequest request, HttpServerResponse response) {
		return this.getDelegate().handle(request, response);
	}
}