package com.swak.security.filter.authc;

import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.security.filter.PathMatchingFilter;

/**
 * 匿名用户
 * @author lifeng
 */
public class AnonymousFilter extends PathMatchingFilter {

	@Override
	protected boolean onPreHandle(HttpServerRequest request, HttpServerResponse response, Object mappedValue) {
		return true;
	}
}
