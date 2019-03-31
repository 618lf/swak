package com.swak.flux.web.result;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.reactivestreams.Publisher;

import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.transport.http.server.HttpServerResponse;
import com.swak.flux.web.HandlerResultHandler;
import com.swak.flux.web.converter.HttpMessageConverter;
import com.swak.utils.Lists;

import reactor.core.publisher.Mono;

/**
 * 结果处理
 * 
 * @author lifeng
 */
public class RequestBodyHandlerResult implements HandlerResultHandler {

	private List<HttpMessageConverter> converters;

	public RequestBodyHandlerResult() {
		converters = Lists.newArrayList();
	}

	public void addConverter(HttpMessageConverter messageConverter) {
		converters.add(messageConverter);
	}

	/**
	 * 这个地方的逻辑是把HandlerResult转换到执行链中
	 */
	@Override
	public Mono<Void> handle(HttpServerRequest request, HttpServerResponse response, Object result) {
		return transformMono(result).flatMap(t -> {
			handleResult(response, t);
			return Mono.empty();
		});
	}

	private Mono<?> transformMono(Object result) {
		if (result != null && result instanceof Mono) {
			return (Mono<?>) result;
		} else if (result != null && result instanceof Publisher) {
			return Mono.from((Publisher<?>) result);
		} else if (result != null && result instanceof CompletableFuture) {
			return Mono.fromFuture((CompletableFuture<?>) result);
		} else if (result != null) {
			return Mono.just(result);
		}
		return Mono.empty();
	}

	private void handleResult(HttpServerResponse response, Object returnValue) {
		
		// donot deal null
		if (returnValue == null) {return;}
		
		// find one Message Converter
		for (HttpMessageConverter messageConverter : this.converters) {
			if (messageConverter.canWrite(returnValue.getClass())) {
				messageConverter.write(returnValue, response);
				return;
			}
		}
	}

	@Override
	public boolean supports(Object result) {
		return true;
	}
}