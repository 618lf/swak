package com.swak.reactivex.web.statics;

import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.reactivex.web.Handler;
import com.swak.reactivex.web.HandlerAdapter;

/**
 * 静态资源适配器
 * 
 * @author lifeng
 */
public class StaticHanlderAdapter implements HandlerAdapter {

	/**
	 * 支持 StaticHandler
	 */
	@Override
	public boolean supports(Handler handler) {
		return handler instanceof StaticHandler;
	}

	/**
	 * 处理请求， 可以启用单独的线程来处理静态资源
	 */
	@Override
	public Object handle(HttpServerRequest request, HttpServerResponse response, Handler handler) {
		return ((StaticHandler) handler).handle(request);
	}
}
