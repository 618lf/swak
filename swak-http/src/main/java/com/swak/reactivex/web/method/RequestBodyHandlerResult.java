package com.swak.reactivex.web.method;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;
import com.swak.reactivex.web.HandlerResultHandler;
import com.swak.reactivex.web.method.converter.HttpMessageConverter;
import com.swak.reactivex.web.method.converter.Jaxb2RootElementHttpMessageConverter;
import com.swak.reactivex.web.method.converter.JsonHttpMessageConverter;
import com.swak.reactivex.web.method.converter.StringHttpMessageConverter;
import com.swak.reactivex.web.method.resolver.RequestResponseBodyMethodReturnValueResolver;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * 结果处理
 * @author lifeng
 */
public class RequestBodyHandlerResult implements HandlerResultHandler, ApplicationContextAware{

	private HandlerMethodReturnValueResolver returnValueResolver;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.initValueResolvers();
	}
	
	public void initValueResolvers() {
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(new StringHttpMessageConverter());
		converters.add(new Jaxb2RootElementHttpMessageConverter());
		converters.add(new JsonHttpMessageConverter());
	    this.returnValueResolver = new RequestResponseBodyMethodReturnValueResolver(converters);
	}
	
	/**
	 * 这个地方的逻辑是把HandlerResult转换到执行链中
	 */
	@Override
	public Observable<Void> handle(HttpServerRequest request, HttpServerResponse response, HandlerResult result) {
		Observable<Void> empty = Observable.empty();
		Object _result = result.getReturnValue();
		Class<?> _type = result.getHandler().getReturnValue().getNestedParameterType();
		if(_result != null && _result instanceof Observable) {
			Observable<?> resultObservable = (Observable<?>)_result;
			return resultObservable.flatMap(new Function<Object, ObservableSource<Void>>() {
				@Override
				public ObservableSource<Void> apply(Object t) throws Exception {
					handleResult(response, _type, t);
					return empty;
				}
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
