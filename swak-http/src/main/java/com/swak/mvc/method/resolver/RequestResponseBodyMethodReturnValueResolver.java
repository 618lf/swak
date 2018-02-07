package com.swak.mvc.method.resolver;

import java.util.List;

import org.springframework.core.MethodParameter;

import com.swak.http.HttpServletResponse;
import com.swak.mvc.method.HandlerMethodReturnValueResolver;
import com.swak.mvc.method.converter.HttpMessageConverter;

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
	public boolean supportsReturnType(MethodParameter returnType) {
		return true;
	}

	/**
	 * 通过 messageConverters 处理输出的数据
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void handleReturnValue(Object value, MethodParameter returnType, HttpServletResponse response)
			throws Exception {
		Class<?> returnValueType = this.getReturnValueType(value, returnType);
		for (HttpMessageConverter<?> messageConverter : this.messageConverters) {
			if (messageConverter.canWrite(returnValueType)) {
				((HttpMessageConverter)messageConverter).write(value, response);
				return;
			}
		}
	}
	
	/**
	 * 如果 handler 返回为空数据
	 * @param value
	 * @param returnType
	 * @return
	 */
	protected Class<?> getReturnValueType(Object value, MethodParameter returnType) {
		return (value != null ? value.getClass() : returnType.getParameterType());
	}
}