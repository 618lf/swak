package com.swak.reactivex.web.function;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;
import com.swak.reactivex.web.Handler;
import com.swak.reactivex.web.HandlerAdapter;
import com.swak.reactivex.web.method.HandlerResult;

/**
 * HandlerFunction 的处理方式
 * @author lifeng
 */
public class HandlerFunctionAdapter implements HandlerAdapter{

	@Override
	public boolean supports(Handler handler) {
		return handler instanceof HandlerFunction;
	}

	@Override
	public HandlerResult handle(HttpServerRequest request, HttpServerResponse response, Handler handler) {
		HandlerFunction _handler = (HandlerFunction) handler;
		Object returnValue = _handler.handle(request);
		return new HandlerResult(_handler, returnValue);
	}
}