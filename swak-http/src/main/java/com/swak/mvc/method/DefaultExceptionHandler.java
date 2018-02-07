package com.swak.mvc.method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

public class DefaultExceptionHandler implements HandlerException {

	Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);
	
	@Override
	public Object resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		// 输出错误
		logger.error(request.getRequestURI(), ex);
		return "server error!";
	}
}