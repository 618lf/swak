package com.swak.mvc.method.resolver;

import java.io.InputStream;

import org.springframework.core.MethodParameter;

import com.swak.http.HttpServletRequest;
import com.swak.mvc.method.HandlerMethodArgumentResolver;

/**
 * 请求的相关对象
 * @author lifeng
 */
public class ServletRequestMethodArgumentResolver implements HandlerMethodArgumentResolver {

	/**
	 * HttpServletRequest 、 InputStream
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();
		return HttpServletRequest.class.isAssignableFrom(paramType)
				|| InputStream.class.isAssignableFrom(paramType);
	}

	/**
	 * 返回对应的对象
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, HttpServletRequest webRequest) throws Exception {
		Class<?> paramType = parameter.getParameterType();
		if (HttpServletRequest.class.isAssignableFrom(paramType)) {
			return webRequest;
		} else if(InputStream.class.isAssignableFrom(paramType)) {
			return webRequest.getInputStream();
		}
		return null;
	}
}