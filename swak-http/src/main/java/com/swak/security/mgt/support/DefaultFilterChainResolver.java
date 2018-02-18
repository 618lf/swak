package com.swak.security.mgt.support;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.swak.common.utils.StringUtils;
import com.swak.http.Filter;
import com.swak.http.FilterChain;
import com.swak.http.HttpServletRequest;
import com.swak.security.exception.ConfigurationException;
import com.swak.security.filter.NamedFilterList;
import com.swak.security.filter.PathConfigProcessor;
import com.swak.security.filter.SimpleNamedFilterList;
import com.swak.security.mgt.DefaultFilter;
import com.swak.security.mgt.FilterChainResolver;

public class DefaultFilterChainResolver implements FilterChainResolver {

	private Map<String, Filter> filters;
	private Map<String, NamedFilterList> filterChains;
	private PathMatcher patternMatcher;

	public DefaultFilterChainResolver() {
		this.filters = new LinkedHashMap<String, Filter>();
		this.filterChains = new LinkedHashMap<String, NamedFilterList>();
		this.patternMatcher = new AntPathMatcher();
		this.addDefaultFilters();
	}

	public Filter getFilter(String name) {
		return this.filters.get(name);
	}

	/**
	 * 添加filter
	 */
	public void addFilter(String name, Filter filter) {
		addFilter(name, filter, false);
	}

	/**
	 * 构建执行链
	 */
	public void createChain(String url, String chainDefinition) {
		if (!StringUtils.hasText(url)) {
			throw new NullPointerException("chainName cannot be null or empty.");
		}
		if (!StringUtils.hasText(chainDefinition)) {
			throw new NullPointerException("chainDefinition cannot be null or empty.");
		}

		String[] filterTokens = splitChainDefinition(chainDefinition);
		for (String token : filterTokens) {
			String[] nameConfigPair = toNameConfigPair(token);
			addToChain(url, nameConfigPair[0], nameConfigPair[1]);
		}
	}

	/**
	 * user, roles["admin"] => split by ','
	 * @param chainDefinition
	 * @return
	 */
	private String[] splitChainDefinition(String chainDefinition) {
		return StringUtils.split(chainDefinition, StringUtils.DEFAULT_DELIMITER_CHAR, '[', ']', true, true);
	}

	/**
	 * roles["admin"] => [roles, admin]
	 * @param token
	 * @return
	 * @throws ConfigurationException
	 */
	private String[] toNameConfigPair(String token) throws ConfigurationException {
		String[] pair = token.split("\\[", 2);
		String name = StringUtils.clean(pair[0]);
		
		if (name == null) {
			throw new IllegalArgumentException("Filter name not found for filter chain definition token: " + token);
		}
		String config = null;

		if (pair.length == 2) {
			config = StringUtils.clean(pair[1]);
			config = config.substring(0, config.length() - 1);
			config = StringUtils.clean(config);

			if (config != null && config.startsWith("\"") && config.endsWith("\"")) {
				String stripped = config.substring(1, config.length() - 1);
				stripped = StringUtils.clean(stripped);

				if (stripped != null && stripped.indexOf('"') == -1) {
					config = stripped;
				}
			}
		}
		return new String[] { name, config };
	}

	private void addToChain(String chainName, String filterName, String filterConfig) {
		if (!StringUtils.hasText(chainName)) {
			throw new IllegalArgumentException("chainName cannot be null or empty.");
		}
		
		Filter filter = getFilter(filterName);
		if (filter == null) {
			throw new IllegalArgumentException("There is no filter with name '" + filterName + "' to apply to chain ["
					+ chainName + "] in the pool of available Filters.  Ensure a "
					+ "filter with that name/path has first been registered with the addFilter method(s).");
		}

		// 设置filter 的 path 对应的权限信息
		if (filter instanceof PathConfigProcessor) {
			((PathConfigProcessor) filter).processPathConfig(chainName, filterConfig);
		}

		// 设置执行链
		ensureChain(chainName, filter);
	}

	protected void ensureChain(String chainName, Filter filter) {
		NamedFilterList chain = this.filterChains.get(chainName);
		if (chain == null) {
			chain = new SimpleNamedFilterList(chainName);
			this.filterChains.put(chainName, chain);
		}
		chain.add(filter);
	}

	
	public boolean hasChains() {
		return !this.filterChains.isEmpty();
	}

	/**
	 * 添加默认的filter
	 */
	protected void addDefaultFilters() {
		try {
			for (DefaultFilter defaultFilter : DefaultFilter.values()) {
				addFilter(defaultFilter.name(), defaultFilter.newInstance(), false);
			}
		} catch (Exception e) {
		}
	}
	
	/**
	 * 添加filter
	 * @param name
	 * @param filter
	 * @param overwrite
	 */
	protected void addFilter(String name, Filter filter, boolean overwrite) {
		Filter existing = getFilter(name);
		if (existing == null || overwrite) {
			this.filters.put(name, filter);
		}
	}
	
	/**
	 * 根据请求生成代理FilterChain
	 * @param request
	 * @param originalChain
	 * @return
	 */
	public FilterChain proxy(HttpServletRequest request, FilterChain original) {
		if (!this.hasChains()) {
			return null;
		}
		String requestURI = request.getRequestURI();
		for (String chainName : this.filterChains.keySet()) {
			if (patternMatcher.match(chainName, requestURI)) {
				return this.proxy(original, chainName);
			}
		}
		return null;
	}
	
	
	/**
	 * 生成代理FilterChain
	 */
	private FilterChain proxy(FilterChain original, String chainName) {
		NamedFilterList configured = this.filterChains.get(chainName);
		if (configured == null) {
			return null;
		}
		return configured.proxy(original);
	}
}