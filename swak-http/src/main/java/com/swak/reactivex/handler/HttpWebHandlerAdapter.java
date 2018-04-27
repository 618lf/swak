package com.swak.reactivex.handler;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

import io.reactivex.Observable;

/**
 * httpHandler -> webHandler
 * @author lifeng
 */
public class HttpWebHandlerAdapter extends WebHandlerDecorator implements HttpHandler {

	public HttpWebHandlerAdapter(WebHandler delegate) {
		super(delegate);
	}

	@Override
	public Observable<Void> apply(HttpServletRequest request, HttpServletResponse response) {
		return this.getDelegate().handle(request, response);
	}
}