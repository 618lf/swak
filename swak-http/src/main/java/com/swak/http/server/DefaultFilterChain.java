package com.swak.http.server;

import com.swak.http.Filter;
import com.swak.http.FilterChain;
import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.http.Servlet;

/**
 * 默认的执行链
 * @author lifeng
 */
public class DefaultFilterChain implements FilterChain {

	private Filter filter;
	private Servlet servlet;
	private int pos = -1;
	
	public DefaultFilterChain(Filter filter, Servlet servlet) {
		this.filter = filter;
		this.servlet = servlet;
		if (this.filter != null) {
			pos = 0;
		}
	}
	
	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response) {
		if (pos >= 0) {
			pos--;
			this.filter.doFilter(request, response, this);
		} else {
			this.servlet.doService(request, response);
		}
	}
}