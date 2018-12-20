package com.swak.reactivex.web.function;

import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.reactivex.web.Handler;
import com.swak.reactivex.web.HandlerAdapter;

/**
 * HandlerFunction 的处理方式
 * 
 * @author lifeng
 */
public class HandlerFunctionAdapter implements HandlerAdapter {

	@Override
	public boolean supports(Handler handler) {
		return handler instanceof HandlerFunction;
	}

	@Override
	public Object handle(HttpServerRequest request, HttpServerResponse response, Handler handler) {
		HandlerFunction _handler = (HandlerFunction) handler;
		return _handler.handle(request);
	}
}