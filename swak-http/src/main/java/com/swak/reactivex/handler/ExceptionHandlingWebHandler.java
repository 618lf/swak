package com.swak.reactivex.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.swak.common.eventbus.system.SystemEventPublisher;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;

import reactor.core.publisher.Mono;

public class ExceptionHandlingWebHandler extends WebHandlerDecorator {

	private final List<WebExceptionHandler> exceptionHandlers;
	private final SystemEventPublisher eventPublisher;

	public ExceptionHandlingWebHandler(WebHandler delegate, List<WebExceptionHandler> handlers,
			SystemEventPublisher eventPublisher) {
		super(delegate);
		this.eventPublisher = eventPublisher;
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
			eventPublisher.publishError(ex);
			completion = Mono.error(ex);
		}

		for (WebExceptionHandler handler : this.exceptionHandlers) {
			completion = completion.onErrorResume(t -> handler.handle(request, response, t));
		}

		return completion;
	}
}
