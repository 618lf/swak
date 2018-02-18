package com.swak.http;

public interface FilterChain {

	/**
	 * 执行filter链
	 * @param request
	 * @param response
	 */
	public void doFilter(HttpServletRequest request, HttpServletResponse response);
}