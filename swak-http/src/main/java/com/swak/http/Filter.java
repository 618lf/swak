package com.swak.http;

/**
 * 模拟 Filter
 * 
 * @author lifeng
 */
public interface Filter {

	/**
	 * 执行filter链
	 * @param request
	 * @param response
	 */
	void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain);
}