package com.swak.flux.security.filter;

import java.util.List;

import com.swak.flux.handler.WebFilter;
import com.swak.flux.handler.WebFilterChain;

public interface NamedFilterList extends List<WebFilter> {

	/**
	 * 当前代理队列的名称
	 * @return
	 */
	String getName();
	
	/**
	 * 构建filterChain
	 * @param filterChain
	 * @return
	 */
	WebFilterChain proxy(WebFilterChain filterChain);
}
