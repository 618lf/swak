package com.swak.security.mgt.support;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.PathMatcher;

import com.swak.reactivex.handler.WebFilter;
import com.swak.reactivex.handler.WebFilterChain;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.web.method.pattern.PathMatcherHelper;
import com.swak.security.filter.NamedFilterList;
import com.swak.security.filter.PathConfigProcessor;
import com.swak.security.filter.SimpleNamedFilterList;
import com.swak.security.filter.authc.AnonymousFilter;
import com.swak.security.filter.authc.AuthenticatingFilter;
import com.swak.security.filter.authc.LogoutFilter;
import com.swak.security.filter.authc.UserFilter;
import com.swak.security.filter.authz.PermissionsAuthorizationFilter;
import com.swak.security.filter.authz.RolesAuthorizationFilter;
import com.swak.security.mgt.FilterChainManager;
import com.swak.utils.StringUtils;

public class DefaultFilterChainManager implements FilterChainManager {

	private Map<String, WebFilter> filters;
	private Map<String, NamedFilterList> filterChains;
	private PathMatcher patternMatcher;

	public DefaultFilterChainManager() {
		this.filters = new LinkedHashMap<String, WebFilter>();
		this.filterChains = new LinkedHashMap<String, NamedFilterList>();
		this.patternMatcher = PathMatcherHelper.getMatcher();
		this.addDefaultFilters();
	}

	/**
	 * 添加默认的filter
	 */
	protected void addDefaultFilters() {
		this.addFilter("anon", new AnonymousFilter());
		this.addFilter("authc", new AuthenticatingFilter());
		this.addFilter("logout", new LogoutFilter());
		this.addFilter("perms", new PermissionsAuthorizationFilter());
		this.addFilter("roles", new RolesAuthorizationFilter());
		this.addFilter("user", new UserFilter());
	}

	public WebFilter getFilter(String name) {
		return this.filters.get(name);
	}

	/**
	 * 添加filter
	 */
	@Override
	public void addFilter(String name, WebFilter filter) {
		addFilter(name, filter, false);
	}

	/**
	 * 添加filter
	 * 
	 * @param name
	 * @param filter
	 * @param overwrite
	 */
	protected void addFilter(String name, WebFilter filter, boolean overwrite) {
		WebFilter existing = getFilter(name);
		if (existing == null || overwrite) {
			this.filters.put(name, filter);
		}
	}

	@Override
	public boolean hasChains() {
		return !this.filterChains.isEmpty();
	}

	/**
	 * 代理当前的 WebFilterChain
	 */
	@Override
	public WebFilterChain proxy(HttpServerRequest request, WebFilterChain original) {
		if (!this.hasChains()) {
			return null;
		}
		String requestURI = request.getRequestURL();
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
	private WebFilterChain proxy(WebFilterChain original, String chainName) {
		NamedFilterList configured = this.filterChains.get(chainName);
		if (configured == null) {
			return null;
		}
		return configured.proxy(original);
	}

	@Override
	public void createChain(String chainName, String chainDefinition) {
		if (!StringUtils.hasText(chainName)) {
			throw new NullPointerException("chainName cannot be null or empty.");
		}
		if (!StringUtils.hasText(chainDefinition)) {
			throw new NullPointerException("chainDefinition cannot be null or empty.");
		}

		String[] filterTokens = splitChainDefinition(chainDefinition);
		for (String token : filterTokens) {
			String[] nameConfigPair = toNameConfigPair(token);
			addToChain(chainName, nameConfigPair[0], nameConfigPair[1]);
		}
	}

	/**
	 * user, roles["admin"] => split by ','
	 * 
	 * @param chainDefinition
	 * @return
	 */
	private String[] splitChainDefinition(String chainDefinition) {
		return StringUtils.split(chainDefinition, StringUtils.DEFAULT_DELIMITER_CHAR, '[', ']', true, true);
	}

	/**
	 * roles["admin"] => [roles, admin]
	 * 
	 * @param token
	 * @return
	 */
	private String[] toNameConfigPair(String token) {
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

		WebFilter filter = getFilter(filterName);
		if (filter == null) {
			throw new IllegalArgumentException("There is no filter with name '" + filterName + "' to apply to chain ["
					+ chainName + "] in the pool of available Filters.  Ensure a "
					+ "filter with that name/path has first been registered with the addFilter method(s).");
		}

		// 设置filter 的 path 对应的权限信息
		if (filter instanceof PathConfigProcessor && StringUtils.hasText(filterConfig)) {
			((PathConfigProcessor) filter).processPathConfig(chainName, filterConfig);
			((PathConfigProcessor) filter).setPathMatcher(this.patternMatcher);
		}

		// 设置执行链
		ensureChain(chainName, filter);
	}

	protected void ensureChain(String chainName, WebFilter filter) {
		NamedFilterList chain = this.filterChains.get(chainName);
		if (chain == null) {
			chain = new SimpleNamedFilterList(chainName);
			this.filterChains.put(chainName, chain);
		}
		chain.add(filter);
	}
}