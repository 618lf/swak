package com.swak.reactivex.handler;

import com.swak.reactivex.server.HttpServerOperations;

import io.reactivex.Observable;

/**
 * 处理 http 请求
 * @author lifeng
 */
public interface HttpHandler {

	/**
	 * 处理请求返回可订阅对象
	 * @param request
	 * @param response
	 * @return
	 */
	Observable<Void> apply(HttpServerOperations httpServerOptions);
}