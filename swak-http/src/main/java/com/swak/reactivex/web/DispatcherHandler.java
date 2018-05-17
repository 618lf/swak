package com.swak.reactivex.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import com.swak.common.exception.BaseRuntimeException;
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

	private static final Exception HANDLER_NOT_FOUND_EXCEPTION = new BaseRuntimeException("No matching handler");

	private List<HandlerMapping> mappings;
	private List<HandlerAdapter> adapters;
	private List<HandlerResultHandler> resultHandlers;

	public DispatcherHandler(ApplicationContext applicationContext) {
		this.initStrategies(applicationContext);
	}

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

	protected void initStrategies(ApplicationContext context) {
		Map<String, HandlerMapping> mappingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context,
				HandlerMapping.class, true, false);

		ArrayList<HandlerMapping> mappings = new ArrayList<>(mappingBeans.values());
		AnnotationAwareOrderComparator.sort(mappings);
		this.mappings = Collections.unmodifiableList(mappings);

		Map<String, HandlerAdapter> adapterBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context,
				HandlerAdapter.class, true, false);

		this.adapters = new ArrayList<>(adapterBeans.values());
		AnnotationAwareOrderComparator.sort(this.adapters);

		Map<String, HandlerResultHandler> beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context,
				HandlerResultHandler.class, true, false);

		this.resultHandlers = new ArrayList<>(beans.values());
		AnnotationAwareOrderComparator.sort(this.resultHandlers);
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
		return Mono.error(HANDLER_NOT_FOUND_EXCEPTION);
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
		return Mono.error(HANDLER_NOT_FOUND_EXCEPTION);
	}
}
