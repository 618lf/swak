package com.swak.security.filter;

import java.util.List;

import com.swak.http.Filter;
import com.swak.http.FilterChain;

public interface NamedFilterList extends List<Filter> {

	String getName();
	
	FilterChain proxy(FilterChain filterChain);
}