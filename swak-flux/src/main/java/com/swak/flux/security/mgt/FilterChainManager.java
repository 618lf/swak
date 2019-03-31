package com.swak.flux.security.mgt;

import com.swak.flux.handler.WebFilter;
import com.swak.flux.handler.WebFilterChain;
import com.swak.flux.transport.http.server.HttpServerRequest;

public interface FilterChainManager {

	/**
	 * 是否有chain
	 * @return
	 */
	boolean hasChains();
	
	/**
	 * 代理当前请求的filterChain
	 * @param request
	 * @param original
	 * @return
	 */
	WebFilterChain proxy(HttpServerRequest request, WebFilterChain original);

	/**
	 * 添加filter
	 * @param name
	 * @param filter
	 */
	void addFilter(String name, WebFilter filter);

	/**
	 * 创建执行链
	 * @param chainName
	 * @param chainDefinition
	 */
	void createChain(String chainName, String chainDefinition);
}
