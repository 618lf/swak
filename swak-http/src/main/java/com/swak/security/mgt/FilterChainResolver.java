package com.swak.security.mgt;

import org.springframework.util.PathMatcher;

import com.swak.http.FilterChain;
import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

public interface FilterChainResolver {

	FilterChain getChain(HttpServletRequest request, HttpServletResponse response,
			FilterChain originalChain);

	void setFilterChainManager(FilterChainManager filterChainManager);

	void setPatternMatcher(PathMatcher patternMatcher);
}
