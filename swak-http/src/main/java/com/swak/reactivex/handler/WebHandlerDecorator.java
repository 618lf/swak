package com.swak.reactivex.handler;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

import io.reactivex.Observable;

/**
 * 代理实际的 WebHandler
 * @author lifeng
 */
public class WebHandlerDecorator implements WebHandler {

	private final WebHandler delegate;
	
	public WebHandlerDecorator(WebHandler delegate) {
		this.delegate = delegate;
	}
	
	public WebHandler getDelegate() {
		return this.delegate;
	}
	
	@Override
	public Observable<Void> handle(HttpServletRequest request, HttpServletResponse response) {
		return this.delegate.handle(request, response);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [delegate=" + this.delegate + "]";
	}
}