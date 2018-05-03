package com.swak.reactivex.web.function;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.web.Handler;

import io.reactivex.Observable;

public interface HandlerFunction extends Handler {

	/**
	 * Handle the given request.
	 * @param request the request to handle
	 * @return the response
	 */
	Observable<Void> handle(HttpServerRequest request);
}
