package com.swak.flux.web;

import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.transport.server.HttpServerResponse;

/**
 * 异常处理
 * @author lifeng
 */
public interface HandlerException {

	Object resolveException(HttpServerRequest request, HttpServerResponse response, Object handler, Exception ex);
}
