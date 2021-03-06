package com.swak.flux.web.method;

import com.swak.flux.transport.server.HttpServerRequest;

/**
 * 实现参数的自动绑定
 * @author lifeng
 */
public interface HandlerMethodArgumentResolver {

	/**
	 * 是否支持这种处理方式
	 * @param parameter
	 * @return
	 */
	boolean supportsParameter(MethodParameter parameter);
	
	/**
	 * 处理请求的参数
	 * @param parameter
	 * @param webRequest
	 * @return
	 * @throws Exception
	 */
	Object resolveArgument(MethodParameter parameter, HttpServerRequest webRequest);
}