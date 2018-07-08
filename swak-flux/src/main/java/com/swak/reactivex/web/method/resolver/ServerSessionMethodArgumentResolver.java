package com.swak.reactivex.web.method.resolver;

import org.springframework.core.MethodParameter;

import com.swak.reactivex.transport.http.Session;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.web.method.HandlerMethodArgumentResolver;

/**
 * 请求的相关对象
 * @author lifeng
 */
public class ServerSessionMethodArgumentResolver implements HandlerMethodArgumentResolver {

	/**
	 * HttpServerRequest 、 InputStream
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();
		return Session.class.isAssignableFrom(paramType);
	}

	/**
	 * 返回对应的对象
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, HttpServerRequest webRequest){
		return webRequest.getSubject().getSession();
	}
}