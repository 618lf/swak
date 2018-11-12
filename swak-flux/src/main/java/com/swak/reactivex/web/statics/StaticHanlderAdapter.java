package com.swak.reactivex.web.statics;

import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.reactivex.web.Handler;
import com.swak.reactivex.web.HandlerAdapter;
import com.swak.reactivex.web.result.HandlerResult;

/**
 * 静态资源适配器
 * 
 * @author lifeng
 */
public class StaticHanlderAdapter implements HandlerAdapter{

	private HandlerResult DEFAULT_RESULT = new HandlerResult(null);
	
	/**
	 * 支持 StaticHandler
	 */
	@Override
	public boolean supports(Handler handler) {
		return handler instanceof StaticHandler;
	}

	/**
	 * 处理请求
	 */
	@Override
	public HandlerResult handle(HttpServerRequest request, HttpServerResponse response, Handler handler) {
		((StaticHandler)handler).handle(request);
		return DEFAULT_RESULT;
	}
}
