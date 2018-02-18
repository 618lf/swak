package com.swak.security.filter.authc;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.security.filter.PathMatchingFilter;

/**
 * 匿名用户
 * @author lifeng
 */
public class AnonymousFilter extends PathMatchingFilter {

	@Override
	protected boolean onPreHandle(HttpServletRequest request, HttpServletResponse response, Object mappedValue) {
		return true;
	}
}
