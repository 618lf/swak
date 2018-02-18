package com.swak.security.filter;

import java.io.IOException;

import com.swak.http.Filter;
import com.swak.http.FilterChain;
import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

public abstract class OncePerRequestFilter implements Filter {

	public static final String ALREADY_FILTERED_SUFFIX = ".FILTERED";

	/**
	 * 基本的执行逻辑
	 */
	public final void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
		String alreadyFilteredAttributeName = getAlreadyFilteredAttributeName();
		if (request.getAttribute(alreadyFilteredAttributeName) != null) {
			filterChain.doFilter(request, response);
		} else {
			request.setAttribute(alreadyFilteredAttributeName, Boolean.TRUE);
			try {
				doFilterInternal(request, response, filterChain);
			} finally {
				request.removeAttribute(alreadyFilteredAttributeName);
			}
		}
	}

	protected String getAlreadyFilteredAttributeName() {
		return getClass().getName() + ALREADY_FILTERED_SUFFIX;
	}

	/**
	 * 最终时执行的这里
	 * 
	 * @param request
	 * @param response
	 * @param chain
	 * @throws ServletException
	 * @throws IOException
	 */
	protected abstract void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain);
}
