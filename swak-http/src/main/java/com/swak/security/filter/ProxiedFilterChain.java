package com.swak.security.filter;

import java.util.List;

import com.swak.http.Filter;
import com.swak.http.FilterChain;
import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

public class ProxiedFilterChain implements FilterChain {

	private FilterChain orig;
    private List<Filter> filters;
    private int index = 0;
    
    public ProxiedFilterChain(FilterChain orig, List<Filter> filters) {
        if (orig == null) {
            throw new NullPointerException("original FilterChain cannot be null.");
        }
        this.orig = orig;
        this.filters = filters;
        this.index = 0;
    }
    
    public void doFilter(HttpServletRequest request, HttpServletResponse response) {
        if (this.filters == null || this.filters.size() == this.index) {
            this.orig.doFilter(request, response);
        } else {
            this.filters.get(this.index++).doFilter(request, response, this);
        }
    }
}