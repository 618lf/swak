package com.swak.reactivex.web.method.resolver;

import java.util.List;

import com.swak.common.exception.BaseRuntimeException;
import com.swak.reactivex.server.HttpServerResponse;
import com.swak.reactivex.web.method.HandlerMethodReturnValueResolver;
import com.swak.reactivex.web.method.converter.HttpMessageConverter;

/**
 * 数据返回支持的类型string、 xml、 json
 * 对应的处理方式见 messageConverters
 * @author lifeng
 */
public class RequestResponseBodyMethodReturnValueResolver implements HandlerMethodReturnValueResolver{

	protected final List<HttpMessageConverter<?>> messageConverters;
	
	public RequestResponseBodyMethodReturnValueResolver(List<HttpMessageConverter<?>> messageConverters) {
		this.messageConverters = messageConverters;
	}
	
	/**
	 * 所有请求都可以处理
	 */
	@Override
	public boolean supportsReturnType(Class<?> returnType) {
		return true;
	}

	/**
	 * 通过 messageConverters 处理输出的数据
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void handleReturnValue(Object value, Class<?> returnType, HttpServerResponse response) {
		try {
			for (HttpMessageConverter<?> messageConverter : this.messageConverters) {
				if (messageConverter.canWrite(returnType)) {
					((HttpMessageConverter)messageConverter).write(value, response);
					return;
				}
			}
		}catch (Exception e) {
			throw new BaseRuntimeException(e.getMessage());
		}
	}
}