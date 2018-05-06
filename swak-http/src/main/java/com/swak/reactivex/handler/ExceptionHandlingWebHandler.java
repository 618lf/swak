package com.swak.reactivex.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;

import reactor.core.publisher.Mono;

public class ExceptionHandlingWebHandler extends WebHandlerDecorator {

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
	public Mono<Void> handle(HttpServerRequest request, HttpServerResponse response) {
		Mono<Void> completion;

		try {
			completion = super.handle(request, response);
		} catch (Throwable ex) {
			completion = Mono.error(ex);
		}

		for (WebExceptionHandler handler : this.exceptionHandlers) {
			completion = completion.onErrorResume(t -> handler.handle(request, response, t));
		}

		return completion;
	}
}
