package com.swak.flux.web.function;

import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.web.Handler;

public interface HandlerFunction extends Handler {

	String HANDLE_NAME = "handle";
	
	/**
	 * 获得返回值
	 * @param request
	 * @return
	 */
	Object handle(HttpServerRequest request);
	
	/**
	 * 描述
	 * @return
	 */
	default String description() {
		return getClass().getSimpleName();
	}
}