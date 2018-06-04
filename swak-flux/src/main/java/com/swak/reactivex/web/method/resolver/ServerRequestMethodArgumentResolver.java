package com.swak.reactivex.web.method.resolver;

import java.io.InputStream;

import org.springframework.core.MethodParameter;

import com.swak.reactivex.transport.http.HttpServerRequest;
import com.swak.reactivex.web.method.HandlerMethodArgumentResolver;

/**
 * 请求的相关对象
 * @author lifeng
 */
public class ServerRequestMethodArgumentResolver implements HandlerMethodArgumentResolver {

	/**
	 * HttpServerRequest 、 InputStream
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();
		return HttpServerRequest.class.isAssignableFrom(paramType)
				|| InputStream.class.isAssignableFrom(paramType);
	}

	/**
	 * 返回对应的对象
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, HttpServerRequest webRequest){
		Class<?> paramType = parameter.getParameterType();
		if (HttpServerRequest.class.isAssignableFrom(paramType)) {
			return webRequest;
		} else if(InputStream.class.isAssignableFrom(paramType)) {
			return webRequest.getInputStream();
		}
		return null;
	}
}