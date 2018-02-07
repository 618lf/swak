package com.swak.mvc.method;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

/**
 * Handler 的处理器
 * 
 * @author lifeng
 */
public interface HandlerAdapter {

	/**
	 * 处理handler
	 * 
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	void handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception;
}
