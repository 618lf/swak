package com.swak.reactivex.handler;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;

import io.reactivex.Observable;

/**
 * 具体的 http 服务处理
 * @author lifeng
 */
public interface WebHandler {

	/**
	 * 处理请求返回可订阅对象
	 * @param request
	 * @param response
	 * @return
	 */
	Observable<Void> handle(HttpServerRequest request, HttpServerResponse response);
}
