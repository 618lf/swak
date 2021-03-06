package com.swak.flux.web;

import java.io.Closeable;

import com.swak.flux.transport.server.HttpServerRequest;

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
	Handler getHandler(HttpServerRequest request);
}