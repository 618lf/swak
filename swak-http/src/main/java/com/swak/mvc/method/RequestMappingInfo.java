package com.swak.mvc.method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import com.google.common.collect.Sets;

/**
 * mapping 对应 @RequestMapping 的解析
 * @author lifeng
 */
public class RequestMappingInfo implements RequestCondition<RequestMappingInfo> {

	private final Set<String> patterns;
	private final PathMatcher pathMatcher;

	public RequestMappingInfo(String[] patterns, PathMatcher pathMatcher) {
		this(Arrays.asList(patterns), pathMatcher);
	}

	public RequestMappingInfo(Collection<String> patterns, PathMatcher pathMatcher) {
		this.patterns = Collections.unmodifiableSet(prependLeadingSlash(patterns));
		this.pathMatcher = pathMatcher;
	}

	private Set<String> prependLeadingSlash(Collection<String> patterns) {
		if (patterns == null) {
			return Collections.emptySet();
		}
		Set<String> result = new LinkedHashSet<String>(patterns.size());
		for (String pattern : patterns) {
			if (StringUtils.hasLength(pattern) && !pattern.startsWith("/")) {
				pattern = new StringBuilder("/").append(pattern).toString();
			}
			result.add(pattern);
		}
		return result;
	}

	/**
	 * 合并
	 */
	@Override
	public RequestMappingInfo combine(RequestMappingInfo other) {
		Set<String> result = new LinkedHashSet<String>();
		if (!this.patterns.isEmpty() && !other.patterns.isEmpty()) {
			for (String pattern1 : this.patterns) {
				for (String pattern2 : other.patterns) {
					result.add(this.pathMatcher.combine(pattern1, pattern2));
				}
			}
		} else if (!this.patterns.isEmpty()) {
			result.addAll(this.patterns);
		} else if (!other.patterns.isEmpty()) {
			result.addAll(other.patterns);
		} else {
			result.add("");
		}
		return new RequestMappingInfo(result, this.pathMatcher);
	}

	/**
	 * 获得匹配条件
	 */
	@Override
	public Set<String> getMatchingCondition(String lookupPath) {
		if (this.patterns.isEmpty()) {
			return this.patterns;
		}
		List<String> matches = getMatchingPatterns(lookupPath);
		return matches.isEmpty() ? null: Sets.newHashSet(matches);
	}

	private List<String> getMatchingPatterns(String lookupPath) {
		List<String> matches = new ArrayList<String>();
		for (String pattern : this.patterns) {
			String match = getMatchingPattern(pattern, lookupPath);
			if (match != null) {
				matches.add(match);
			}
		}
		Collections.sort(matches, this.pathMatcher.getPatternComparator(lookupPath));
		return matches;
	}

	private String getMatchingPattern(String pattern, String lookupPath) {
		if (pattern.equals(lookupPath)) {
			return pattern;
		}
		if (this.pathMatcher.match(pattern, lookupPath)) {
			return pattern;
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[");
		for (Iterator<?> iterator = patterns.iterator(); iterator.hasNext();) {
			Object expression = iterator.next();
			builder.append(expression.toString());
			if (iterator.hasNext()) {
				builder.append("||");
			}
		}
		builder.append("]");
		return builder.toString();
	}

	public Set<String> getPatterns() {
		return patterns;
	}

	/**
	 * 构建
	 * 
	 * @param paths
	 * @return
	 */
	public static RequestMappingInfo paths(PathMatcher pathMatcher, String... paths) {
		return new RequestMappingInfo(paths, pathMatcher);
	}
}
