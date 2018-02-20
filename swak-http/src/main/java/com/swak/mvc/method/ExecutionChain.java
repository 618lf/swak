package com.swak.mvc.method;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

/**
 * 执行链
 * @author lifeng
 */
public interface ExecutionChain {
	
	/**
	 * 返回执行器
	 * @return
	 */
	HandlerMethod getHandler();

	/**
	 * handler 前置处理器
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	default boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return true;
	}
	
	/**
	 * 后置处理器
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	default void applyPostHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
	}
	
	/**
	 * 结束处理器
	 * @param request
	 * @param response
	 * @param ex
	 * @throws Exception
	 */
	default void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, Exception ex)
			throws Exception {
	}
}