package com.swak.reactivex.web.result;

import java.util.List;

import org.reactivestreams.Publisher;

import com.swak.common.utils.Lists;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.reactivex.web.HandlerResultHandler;
import com.swak.reactivex.web.converter.HttpMessageConverter;

import reactor.core.publisher.Mono;

/**
 * 结果处理
 * 
 * @author lifeng
 */
public class RequestBodyHandlerResult implements HandlerResultHandler {

	private HandlerReturnValueResolver returnValueResolver;
	private List<HttpMessageConverter<?>> converters;

	public RequestBodyHandlerResult() {
		converters = Lists.newArrayList();
		returnValueResolver = new RequestResponseBodyReturnValueResolver(converters);
	}

	public void addConverter(HttpMessageConverter<?> messageConverter) {
		converters.add(messageConverter);
	}

	/**
	 * 这个地方的逻辑是把HandlerResult转换到执行链中
	 */
	@Override
	public Mono<Void> handle(HttpServerRequest request, HttpServerResponse response, HandlerResult result) {
		return transformMono(result).flatMap(t ->{
			handleResult(response, result.getReturnValueType(), t);
			return Mono.empty();
		});
	}
	
	private Mono<?> transformMono(HandlerResult result) {
		Object _result = result.getReturnValue();
		if (_result != null && _result instanceof Mono) {
			return (Mono<?>) _result;
		} else if(_result != null && _result instanceof Publisher) {
			return Mono.from((Publisher<?>)_result);
		} else if(_result != null) {
			return Mono.just(_result);
		}
		return Mono.empty();
	}

	private void handleResult(HttpServerResponse response, Class<?> returnType, Object returnValue) {
		returnValueResolver.handleReturnValue(returnValue, returnType, response);
	}

	@Override
	public boolean supports(HandlerResult result) {
		return true;
	}
}
