package com.swak.security;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.CollectionUtils;

import com.swak.common.utils.Maps;
import com.swak.common.utils.StringUtils;
import com.swak.http.Filter;
import com.swak.security.mgt.FilterChainResolver;
import com.swak.security.mgt.SecurityManager;
import com.swak.security.mgt.support.DefaultFilterChainResolver;
import com.swak.security.utils.SecurityUtils;

/**
 * 初始化安全filter
 * 
 * @author lifeng
 */
public class SecurityFilterFactoryBean implements FactoryBean<SecurityFilter> {

	private SecurityManager securityManager;
	private Map<String, Filter> filters;
	private Map<String, String> filterChainDefinitionMap;
	private SecurityFilter instance;

	public SecurityFilterFactoryBean() {
		this.filters = new LinkedHashMap<String, Filter>();
		this.filterChainDefinitionMap = new LinkedHashMap<String, String>();
	}

	@Override
	public SecurityFilter getObject() throws Exception {
		if (instance == null) {
			instance = createInstance();
		}
		return instance;
	}

	@Override
	public Class<?> getObjectType() {
		return SecurityFilter.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public Map<String, String> getFilterChainDefinitionMap() {
		return filterChainDefinitionMap;
	}

	public void setFilterChainDefinitionMap(Map<String, String> filterChainDefinitionMap) {
		this.filterChainDefinitionMap = filterChainDefinitionMap;
	}

	public Map<String, Filter> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, Filter> filters) {
		this.filters = filters;
	}

	public SecurityManager getSecurityManager() {
		return securityManager;
	}

	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	/**
	 * url 定义的配置 url 配置是有顺序的
	 * 
	 * @param definitions
	 */
	public void setFilterChainDefinitions(String definitions) {
		Map<String, String> filterChainDefinitionMap = Maps.newOrderMap();
		Scanner scanner = new Scanner(definitions);
		while (scanner.hasNextLine()) {
			String line = StringUtils.clean(scanner.nextLine());
			if (!StringUtils.hasText(line)) {
				continue;
			}
			String[] parts = StringUtils.split(line, '=');
			if (!(parts != null && parts.length == 2)) {
				continue;
			}
			String path = StringUtils.clean(parts[0]);
			String filter = StringUtils.clean(parts[1]);
			if (!(StringUtils.hasText(path) && StringUtils.hasText(filter))) {
				continue;
			}
			filterChainDefinitionMap.put(path, filter);
		}
		IOUtils.closeQuietly(scanner);

		// 设置
		this.setFilterChainDefinitionMap(filterChainDefinitionMap);
	}

	private SecurityFilter createInstance() {

		// manager
		SecurityManager securityManager = getSecurityManager();
		SecurityUtils.setSecurityManager(securityManager);

		// filterChain
		FilterChainResolver chainManager = createFilterChainManager();

		// filter
		return new SecurityFilter(securityManager, chainManager);
	}

	private FilterChainResolver createFilterChainManager() {
		FilterChainResolver chainManager = new DefaultFilterChainResolver();

		// 可以配置新的filter
		Map<String, Filter> filters = getFilters();
		if (!CollectionUtils.isEmpty(filters)) {
			for (Map.Entry<String, Filter> entry : filters.entrySet()) {
				String name = entry.getKey();
				Filter filter = entry.getValue();
				chainManager.addFilter(name, filter);
			}
		}

		// 构建filter chain path - filters
		Map<String, String> chains = getFilterChainDefinitionMap();
		if (!CollectionUtils.isEmpty(chains)) {
			for (Map.Entry<String, String> entry : chains.entrySet()) {
				String url = entry.getKey();
				String chainDefinition = entry.getValue();
				chainManager.createChain(url, chainDefinition);
			}
		}

		return chainManager;
	}
}