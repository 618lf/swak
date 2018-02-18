package com.swak.security.mgt;

import com.swak.http.Filter;
import com.swak.http.FilterChain;
import com.swak.http.HttpServletRequest;

public interface FilterChainResolver {

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
	FilterChain proxy(HttpServletRequest request, FilterChain original);

	/**
	 * 添加filter
	 * @param name
	 * @param filter
	 */
	void addFilter(String name, Filter filter);

	/**
	 * 创建执行链
	 * @param chainName
	 * @param chainDefinition
	 */
	void createChain(String chainName, String chainDefinition);
}