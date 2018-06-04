package com.swak.reactivex.handler;

import com.swak.reactivex.transport.http.HttpServerRequest;
import com.swak.reactivex.transport.http.HttpServerResponse;

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