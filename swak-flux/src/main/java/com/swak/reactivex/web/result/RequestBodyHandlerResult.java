package com.swak.reactivex.web.result;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.reactivestreams.Publisher;

import com.swak.reactivex.transport.http.HttpServerRequest;
import com.swak.reactivex.transport.http.HttpServerResponse;
import com.swak.reactivex.web.HandlerResultHandler;
import com.swak.reactivex.web.converter.HttpMessageConverter;
import com.swak.utils.Lists;

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
		return transformMono(result.getReturnValue()).flatMap(t ->{
			handleResult(response, result.getReturnValueType(), t);
			return Mono.empty();
		});
	}
	
	private Mono<?> transformMono(Object result) {
		if (result != null && result instanceof Mono) {
			return (Mono<?>) result;
		} else if(result != null && result instanceof Publisher) {
			return Mono.from((Publisher<?>)result);
		} else if(result != null && result instanceof CompletableFuture) {
			return Mono.fromFuture((CompletableFuture<?>)result);
		} else if(result != null) {
			return Mono.just(result);
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