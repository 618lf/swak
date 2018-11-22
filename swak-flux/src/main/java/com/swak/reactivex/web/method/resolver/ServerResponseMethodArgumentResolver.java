package com.swak.reactivex.web.method.resolver;

import java.io.OutputStream;

import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.reactivex.web.method.HandlerMethodArgumentResolver;
import com.swak.reactivex.web.method.MethodParameter;

/**
 * 支持在方法参数中直接设置 HttpServerResponse 和 OutputStream
 * @author lifeng
 */
public class ServerResponseMethodArgumentResolver implements HandlerMethodArgumentResolver {

	/**
	 * HttpServerResponse、 OutputStream
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();
		return HttpServerResponse.class.isAssignableFrom(paramType) || OutputStream.class.isAssignableFrom(paramType);
	}

	/**
	 * 返回对应的对象
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, HttpServerRequest request){
		Class<?> paramType = parameter.getParameterType();
		if (HttpServerResponse.class.isAssignableFrom(paramType)) {
			return request.getResponse();
		}
		if (OutputStream.class.isAssignableFrom(paramType)) {
			return request.getResponse().getOutputStream();
		}
		return null;
	}
}