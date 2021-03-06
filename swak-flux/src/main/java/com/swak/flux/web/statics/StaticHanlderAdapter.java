package com.swak.flux.web.statics;

import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.transport.server.HttpServerResponse;
import com.swak.flux.web.Handler;
import com.swak.flux.web.HandlerAdapter;

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
