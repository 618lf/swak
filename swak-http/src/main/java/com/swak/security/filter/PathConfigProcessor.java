package com.swak.security.filter;

import org.springframework.util.PathMatcher;

import com.swak.http.Filter;

public interface PathConfigProcessor {

	/**
	 * 填充配置信息
	 * @param path
	 * @param config
	 * @return
	 */
	Filter processPathConfig(String path, String config);
	
	/**
	 * 设置模式匹配器
	 * @param pathMatcher
	 */
	void setPathMatcher(PathMatcher pathMatcher); 
}
