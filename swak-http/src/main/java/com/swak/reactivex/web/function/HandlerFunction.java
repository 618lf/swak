package com.swak.reactivex.web.function;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.web.Handler;

public interface HandlerFunction extends Handler {

	/**
	 * Handle the given request.
	 * @param request the request to handle
	 * @return the response
	 */
	Object handle(HttpServerRequest request);
}