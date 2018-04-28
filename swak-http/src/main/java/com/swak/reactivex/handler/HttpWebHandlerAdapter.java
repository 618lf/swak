package com.swak.reactivex.handler;

import com.swak.reactivex.server.HttpServerOptions;
import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;

import io.reactivex.Observable;

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
	public Observable<Void> apply(HttpServerOptions httpServerOptions) {
		HttpServerRequest request = httpServerOptions.getRequest();
		HttpServerResponse response = httpServerOptions.getResponse();
		return this.getDelegate().handle(request, response);
	}
}