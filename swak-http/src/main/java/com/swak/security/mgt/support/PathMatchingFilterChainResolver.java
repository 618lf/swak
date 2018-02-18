package com.swak.security.mgt.support;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.swak.http.FilterChain;
import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.security.mgt.FilterChainManager;
import com.swak.security.mgt.FilterChainResolver;

public class PathMatchingFilterChainResolver implements FilterChainResolver {

	private FilterChainManager filterChainManager;
	private PathMatcher patternMatcher;

	public PathMatchingFilterChainResolver() {
		patternMatcher = new AntPathMatcher();
	}

	@Override
	public FilterChain getChain(HttpServletRequest request, HttpServletResponse response, FilterChain originalChain) {
		FilterChainManager filterChainManager = getFilterChainManager();
		if (!filterChainManager.hasChains()) {
			return null;
		}

		String requestURI = request.getRequestURI();

		for (String pathPattern : filterChainManager.getChainNames()) {
			if (patternMatcher.match(pathPattern, requestURI)) {
				return filterChainManager.proxy(originalChain, pathPattern);
			}
		}
		return null;
	}

	public FilterChainManager getFilterChainManager() {
		return filterChainManager;
	}

	public void setFilterChainManager(FilterChainManager filterChainManager) {
		this.filterChainManager = filterChainManager;
	}

	public PathMatcher getPatternMatcher() {
		return patternMatcher;
	}

	public void setPatternMatcher(PathMatcher patternMatcher) {
		this.patternMatcher = patternMatcher;
	}
}
