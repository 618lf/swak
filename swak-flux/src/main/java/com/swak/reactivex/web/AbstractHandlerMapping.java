package com.swak.reactivex.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.util.PathMatcher;

import com.swak.reactivex.web.method.pattern.PathMatcherHelper;
import com.swak.reactivex.web.method.pattern.UrlPathHelper;

/**
 * 基本的 AbstractHandler 处理器
 * @author lifeng
 */
public abstract class AbstractHandlerMapping implements HandlerMapping, Ordered{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private PathMatcher pathMatcher = PathMatcherHelper.getMatcher();
	private UrlPathHelper urlPathHelper = new UrlPathHelper();
	
	public PathMatcher getPathMatcher() {
		return this.pathMatcher;
	}

	public UrlPathHelper getUrlPathHelper() {
		return urlPathHelper;
	}
}