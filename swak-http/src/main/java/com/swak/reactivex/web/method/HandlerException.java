package com.swak.reactivex.web.method;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;

/**
 * 异常处理
 * @author lifeng
 */
public interface HandlerException {

	Object resolveException(HttpServerRequest request, HttpServerResponse response, Object handler, Exception ex);
}
