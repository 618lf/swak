package com.swak.reactivex.web.result;

import java.util.List;

import com.swak.common.utils.Lists;
import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;
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
		Mono<Void> empty = Mono.empty();
		Object _result = result.getReturnValue();
		Class<?> _type = result.getReturnValueType();
		if (_result != null && _result instanceof Mono) {
			Mono<?> resultObservable = (Mono<?>) _result;
			return resultObservable.flatMap(t -> {
				handleResult(response, _type, t);
				return empty;
			});
		} else {
			handleResult(response, _type, _result);
			return empty;
		}
	}

	private void handleResult(HttpServerResponse response, Class<?> returnType, Object returnValue) {
		returnValueResolver.handleReturnValue(returnValue, returnType, response);
	}

	@Override
	public boolean supports(HandlerResult result) {
		return true;
	}
}
