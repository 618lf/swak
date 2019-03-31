package com.swak.flux.security.filter;

import org.springframework.util.PathMatcher;

import com.swak.flux.handler.WebFilter;

public interface PathConfigProcessor {

	/**
	 * 填充配置信息
	 * @param path
	 * @param config
	 * @return
	 */
	WebFilter processPathConfig(String path, String config);
	
	/**
	 * 设置模式匹配器
	 * @param pathMatcher
	 */
	void setPathMatcher(PathMatcher pathMatcher); 
}