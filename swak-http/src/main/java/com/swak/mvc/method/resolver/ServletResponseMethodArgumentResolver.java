package com.swak.mvc.method.resolver;

import java.io.OutputStream;

import org.springframework.core.MethodParameter;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.mvc.method.HandlerMethodArgumentResolver;

/**
 * 支持在方法参数中直接设置 HttpServletResponse 和 OutputStream
 * @author lifeng
 */
public class ServletResponseMethodArgumentResolver implements HandlerMethodArgumentResolver {

	/**
	 * HttpServletResponse、 OutputStream
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();
		return HttpServletResponse.class.isAssignableFrom(paramType) || OutputStream.class.isAssignableFrom(paramType);
	}

	/**
	 * 返回对应的对象
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, HttpServletRequest webRequest) throws Exception {
		Class<?> paramType = parameter.getParameterType();
		if (HttpServletResponse.class.isAssignableFrom(paramType)) {
			return webRequest.getResponse();
		}
		if (OutputStream.class.isAssignableFrom(paramType)) {
			return webRequest.getResponse().getOutputStream();
		}
		return null;
	}
}