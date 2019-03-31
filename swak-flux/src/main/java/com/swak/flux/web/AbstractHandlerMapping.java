package com.swak.flux.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.util.PathMatcher;

import com.swak.flux.web.method.pattern.PathMatcherHelper;

/**
 * 基本的 AbstractHandler 处理器
 * @author lifeng
 */
public abstract class AbstractHandlerMapping implements HandlerMapping, Ordered{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private PathMatcher pathMatcher = PathMatcherHelper.getMatcher();
	
	public PathMatcher getPathMatcher() {
		return this.pathMatcher;
	}
}