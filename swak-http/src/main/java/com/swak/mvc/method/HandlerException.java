package com.swak.mvc.method;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

/**
 * 异常处理
 * @author lifeng
 */
public interface HandlerException {

	Object resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex);
}
