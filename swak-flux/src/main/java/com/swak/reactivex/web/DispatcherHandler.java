package com.swak.reactivex.web;

import java.util.List;

import com.swak.reactivex.HttpConst;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.reactivex.handler.WebHandler;
import com.swak.reactivex.web.result.HandlerResult;

import reactor.core.publisher.Mono;

/**
 * mvc 式的处理方式
 * 
 * @author lifeng
 */
public class DispatcherHandler implements WebHandler {

	private List<HandlerMapping> mappings;
	private List<HandlerAdapter> adapters;
	private List<HandlerResultHandler> resultHandlers;

	public List<HandlerMapping> getMappings() {
		return mappings;
	}

	public void setMappings(List<HandlerMapping> mappings) {
		this.mappings = mappings;
	}

	public List<HandlerAdapter> getAdapters() {
		return adapters;
	}

	public void setAdapters(List<HandlerAdapter> adapters) {
		this.adapters = adapters;
	}

	public List<HandlerResultHandler> getResultHandlers() {
		return resultHandlers;
	}

	public void setResultHandlers(List<HandlerResultHandler> resultHandlers) {
		this.resultHandlers = resultHandlers;
	}
	
	@Override
	public Mono<Void> handle(HttpServerRequest request, HttpServerResponse response) {
		return this.handleMappering(request, response)
			   .map(handler -> this.invokeHandler(request, response, handler))
			   .flatMap(result -> this.handleResult(request, response, result));
	}

	public Mono<Handler> handleMappering(HttpServerRequest request, HttpServerResponse response) {
		for (HandlerMapping mapping : mappings) {
			Handler handler = mapping.getHandler(request);
			if (handler != null) {
				return Mono.just(handler);
			}
		}
		return Mono.error(HttpConst.HANDLER_NOT_FOUND_EXCEPTION);
	}

	public HandlerResult invokeHandler(HttpServerRequest request, HttpServerResponse response,
			Handler handler) {
		for (HandlerAdapter handlerAdapter : this.adapters) {
			if (handlerAdapter.supports(handler)) {
				return handlerAdapter.handle(request, response, handler);
			}
		}
		
		return null;
	}

	public Mono<Void> handleResult(HttpServerRequest request, HttpServerResponse response,
			HandlerResult result) {
		for (HandlerResultHandler resultHandler : this.resultHandlers) {
			if (resultHandler.supports(result)) {
				return resultHandler.handle(request, response, result);
			}
		}
		return Mono.error(HttpConst.HANDLER_NOT_FOUND_EXCEPTION);
	}
}
