package com.swak.mvc.method;

import org.springframework.util.PathMatcher;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

public class MappedInterceptor implements HandlerInterceptor {

	private final String[] includePatterns;

	private final String[] excludePatterns;

	private final HandlerInterceptor interceptor;

	private PathMatcher pathMatcher;


	/**
	 * Create a new MappedInterceptor instance.
	 * @param includePatterns the path patterns to map with a {@code null} value matching to all paths
	 * @param interceptor the HandlerInterceptor instance to map to the given patterns
	 */
	public MappedInterceptor(String[] includePatterns, HandlerInterceptor interceptor) {
		this(includePatterns, null, interceptor);
	}

	/**
	 * Create a new MappedInterceptor instance.
	 * @param includePatterns the path patterns to map with a {@code null} value matching to all paths
	 * @param excludePatterns the path patterns to exclude
	 * @param interceptor the HandlerInterceptor instance to map to the given patterns
	 */
	public MappedInterceptor(String[] includePatterns, String[] excludePatterns, HandlerInterceptor interceptor) {
		this.includePatterns = includePatterns;
		this.excludePatterns = excludePatterns;
		this.interceptor = interceptor;
	}

	/**
	 * Configure a PathMatcher to use with this MappedInterceptor instead of the
	 * one passed by default to the {@link #matches(String, org.springframework.util.PathMatcher)}
	 * method. This is an advanced property that is only required when using custom
	 * PathMatcher implementations that support mapping metadata other than the
	 * Ant-style path patterns supported by default.
	 *
	 * @param pathMatcher the path matcher to use
	 */
	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

	/**
	 * The configured PathMatcher, or {@code null}.
	 */
	public PathMatcher getPathMatcher() {
		return this.pathMatcher;
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

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return this.interceptor.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		this.interceptor.postHandle(request, response, handler);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		this.interceptor.afterCompletion(request, response, handler, ex);
	}

	/**
	 * Returns {@code true} if the interceptor applies to the given request path.
	 * @param lookupPath the current request path
	 * @param pathMatcher a path matcher for path pattern matching
	 */
	public boolean matches(String lookupPath, PathMatcher pathMatcher) {
		PathMatcher pathMatcherToUse = (this.pathMatcher != null) ? this.pathMatcher : pathMatcher;
		if (this.excludePatterns != null) {
			for (String pattern : this.excludePatterns) {
				if (pathMatcherToUse.match(pattern, lookupPath)) {
					return false;
				}
			}
		}
		if (this.includePatterns == null) {
			return true;
		}
		else {
			for (String pattern : this.includePatterns) {
				if (pathMatcherToUse.match(pattern, lookupPath)) {
					return true;
				}
			}
			return false;
		}
	}
}
