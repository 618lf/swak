package com.swak.flux.web.method;

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

import com.swak.flux.web.annotation.RequestMethod;
import com.swak.flux.web.method.pattern.PathMatcherHelper;
import com.swak.utils.Sets;

/**
 * mapping 对应 @RequestMapping 的解析
 * 
 * @author lifeng
 */
public class RequestMappingInfo implements RequestCondition<RequestMappingInfo> {

	private final Set<String> patterns;
	private final RequestMethod method;
	private final PathMatcher pathMatcher;

	public RequestMappingInfo(String[] patterns, RequestMethod method) {
		this(Arrays.asList(patterns), method);
	}

	public RequestMappingInfo(Collection<String> patterns, RequestMethod method) {
		this.patterns = Collections.unmodifiableSet(prependLeadingSlash(patterns));
		this.method = method;
		this.pathMatcher = PathMatcherHelper.getMatcher();
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
	
	public Set<String> getPatterns() {
		return patterns;
	}

	public RequestMethod getMethod() {
		return method;
	}

	/**
	 * 合并
	 */
	@Override
	public RequestMappingInfo combine(RequestMappingInfo other) {

		// patterns
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

		// method
		RequestMethod method = other.getMethod() == null ? this.getMethod() : other.getMethod();

		return new RequestMappingInfo(result, method);
	}

	/**
	 * 获得匹配条件
	 */
	@Override
	public Match getMatchingCondition(String lookupPath, RequestMethod lookupMethod) {

		// 通用的匹配器
		if (this.patterns.isEmpty() && (this.method == RequestMethod.ALL || lookupMethod == this.method)) {
			return new Match(lookupPath, this.patterns, this.method);
		}

		// 如果有 patterns 则必须要匹配上
		List<String> matches = getMatchingPatterns(lookupPath);
		return (!matches.isEmpty() && (this.method == RequestMethod.ALL || lookupMethod == this.method))
				? new Match(lookupPath, Sets.newHashSet(matches), this.method)
				: null;
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
		StringBuilder builder = new StringBuilder("patterns-[");
		for (Iterator<?> iterator = patterns.iterator(); iterator.hasNext();) {
			Object expression = iterator.next();
			builder.append(expression.toString());
			if (iterator.hasNext()) {
				builder.append("||");
			}
		}
		builder.append("]");
		builder.append(",methos-[");
		builder.append(this.method.name());
		builder.append("]");
		return builder.toString();
	}

	/**
	 * 构建
	 * 
	 * @param paths
	 * @return
	 */
	public static RequestMappingInfo paths(RequestMethod method, String... paths) {
		return new RequestMappingInfo(paths, method);
	}
}
