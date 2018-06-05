package com.swak.reactivex.web;

import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;

/**
 * 异常处理
 * @author lifeng
 */
public interface HandlerException {

	Object resolveException(HttpServerRequest request, HttpServerResponse response, Object handler, Exception ex);
}
