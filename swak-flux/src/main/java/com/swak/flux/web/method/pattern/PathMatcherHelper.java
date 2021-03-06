package com.swak.flux.web.method.pattern;

import java.util.Comparator;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class PathMatcherHelper {
	
	/**
	 * 直接使用spring 的 PathMatcher
	 */
	private static final PathMatcher matcher = new AntPathMatcher();

	/**
	 * 获得一个matcher
	 * 
	 * @return
	 */
	public static PathMatcher getMatcher() {
		return matcher;
	}

	/**
	 * 获得特定的匹配比较器
	 * 
	 * @param path
	 * @return
	 */
	public static Comparator<String> getPatternComparator(String path) {
		return matcher.getPatternComparator(path);
	}
}
