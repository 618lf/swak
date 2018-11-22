package com.swak.reactivex.web.method.resolver;

import com.swak.reactivex.transport.http.Principal;
import com.swak.reactivex.transport.http.Session;
import com.swak.reactivex.transport.http.Subject;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.web.method.HandlerMethodArgumentResolver;
import com.swak.reactivex.web.method.MethodParameter;

/**
 * 请求的相关对象
 * 
 * @author lifeng
 */
public class ServerSessionMethodArgumentResolver implements HandlerMethodArgumentResolver {

	/**
	 * HttpServerRequest 、 InputStream
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();
		return Session.class.isAssignableFrom(paramType) || Subject.class.isAssignableFrom(paramType)
				|| Principal.class.isAssignableFrom(paramType);
	}

	/**
	 * 返回对应的对象
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, HttpServerRequest webRequest) {
		Class<?> paramType = parameter.getParameterType();
		if (Session.class.isAssignableFrom(paramType)) {
			return webRequest.getSubject().getSession();
		} else if (Subject.class.isAssignableFrom(paramType)) {
			return webRequest.getSubject();
		} else if (Principal.class.isAssignableFrom(paramType)) {
			return webRequest.getSubject().getPrincipal();
		}
		return null;
	}
}