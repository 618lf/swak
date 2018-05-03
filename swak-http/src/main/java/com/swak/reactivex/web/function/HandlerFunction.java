package com.swak.reactivex.web.function;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.web.Handler;

public interface HandlerFunction extends Handler {

	String HANDLE_NAME = "handle";
	
	/**
	 * 获得返回值
	 * @param request
	 * @return
	 */
	Object handle(HttpServerRequest request);
}