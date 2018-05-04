package com.swak.reactivex.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class ExceptionHandlingWebHandler extends WebHandlerDecorator{

	private final List<WebExceptionHandler> exceptionHandlers;

	public ExceptionHandlingWebHandler(WebHandler delegate, List<WebExceptionHandler> handlers) {
		super(delegate);
		this.exceptionHandlers = Collections.unmodifiableList(new ArrayList<>(handlers));
	}
	
	/**
	 * Return a read-only list of the configured exception handlers.
	 */
	public List<WebExceptionHandler> getExceptionHandlers() {
		return this.exceptionHandlers;
	}

	/**
	 * 处理请求
	 */
	@Override
	public Observable<Void> handle(HttpServerRequest request, HttpServerResponse response) {
		Observable<Void> completion;
		
		try {
			completion = super.handle(request, response);
		}
		catch (Throwable ex) {
			completion = Observable.error(ex);
		}
		
		for (WebExceptionHandler handler : this.exceptionHandlers) {
			completion = completion.onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Void>>() {
				@Override
				public ObservableSource<? extends Void> apply(Throwable t) throws Exception {
					return handler.handle(request, response, t);
				}
			});
		}

		return completion;
	}
}
