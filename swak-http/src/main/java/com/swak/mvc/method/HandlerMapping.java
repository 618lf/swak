package com.swak.mvc.method;

import com.swak.http.HttpServletRequest;
import com.swak.mvc.HandlerExecutionChain;

/**
 * 根据请求获取Handler的执行链
 * @author lifeng
 */
public interface HandlerMapping {
	
	/**
	 * 构建请求的执行链
	 * @param request
	 * @return
	 * @throws Exception
	 */
	HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}