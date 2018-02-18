package com.swak.security.filter;

import java.util.List;

import com.swak.http.Filter;
import com.swak.http.FilterChain;

public interface NamedFilterList extends List<Filter> {

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
	FilterChain proxy(FilterChain filterChain);
}