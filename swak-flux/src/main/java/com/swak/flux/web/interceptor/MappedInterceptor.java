package com.swak.flux.web.interceptor;

import org.springframework.util.PathMatcher;

import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.web.method.pattern.PathMatcherHelper;
import com.swak.flux.web.method.pattern.UrlPathHelper;

public class MappedInterceptor implements HandlerInterceptor {

	private final String[] includePatterns;

	private final String[] excludePatterns;

	private final HandlerInterceptor interceptor;

	/**
	 * Create a new MappedInterceptor instance.
	 * 
	 * @param includePatterns
	 *            the path patterns to map with a {@code null} value matching to all
	 *            paths
	 * @param interceptor
	 *            the HandlerInterceptor instance to map to the given patterns
	 */
	public MappedInterceptor(String[] includePatterns, HandlerInterceptor interceptor) {
		this(includePatterns, null, interceptor);
	}

	/**
	 * Create a new MappedInterceptor instance.
	 * 
	 * @param includePatterns
	 *            the path patterns to map with a {@code null} value matching to all
	 *            paths
	 * @param excludePatterns
	 *            the path patterns to exclude
	 * @param interceptor
	 *            the HandlerInterceptor instance to map to the given patterns
	 */
	public MappedInterceptor(String[] includePatterns, String[] excludePatterns, HandlerInterceptor interceptor) {
		this.includePatterns = includePatterns;
		this.excludePatterns = excludePatterns;
		this.interceptor = interceptor;
	}

	/**
	 * The path into the application the interceptor is mapped to.
	 */
	public String[] getPathPatterns() {
		return this.includePatterns;
	}

	/**
	 * The actual Interceptor reference.
	 */
	public HandlerInterceptor getInterceptor() {
		return this.interceptor;
	}

	/**
	 * Returns {@code true} if the interceptor applies to the given request path.
	 * @param request
	 * @return
	 */
	public boolean matches(HttpServerRequest request) {
		String lookupPath = UrlPathHelper.getLookupPathForRequest(request);
		PathMatcher pathMatcherToUse = PathMatcherHelper.getMatcher();
		if (this.excludePatterns != null) {
			for (String pattern : this.excludePatterns) {
				if (pathMatcherToUse.match(pattern, lookupPath)) {
					return false;
				}
			}
		}
		if (this.includePatterns == null) {
			return true;
		} else {
			for (String pattern : this.includePatterns) {
				if (pathMatcherToUse.match(pattern, lookupPath)) {
					return true;
				}
			}
			return false;
		}
	}
}
