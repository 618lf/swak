package com.swak.mvc.method;

import java.io.Closeable;

import com.swak.http.HttpServletRequest;

/**
 * 根据请求获取Handler的执行链
 * @author lifeng
 */
public interface HandlerMapping extends Closeable {
	
	/**
	 * 构建请求的执行链
	 * @param request
	 * @return
	 * @throws Exception
	 */
	ExecutionChain getHandler(HttpServletRequest request) throws Exception;
}