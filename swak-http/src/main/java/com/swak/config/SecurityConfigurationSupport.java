package com.swak.config;

import java.util.Map;

import com.swak.common.utils.Maps;
import com.swak.common.utils.StringUtils;
import com.swak.reactivex.handler.WebFilter;
import com.swak.security.realm.Realm;

/**
 * 安全的配置项目
 * @author lifeng
 */
public class SecurityConfigurationSupport {

	private Map<String, WebFilter> filters;
	private Realm realm;
	private Map<String, String> chains;
	
	public Map<String, WebFilter> getFilters() {
		return filters;
	}
	public Realm getRealm() {
		return realm;
	}
	public Map<String, String> getChains() {
		return chains;
	}
	
	/**
	 * 设置域
	 * @param realm
	 */
	public void setRealm(Realm realm) {
		this.realm = realm;
	}
	
	/**
	 * 添加 filter
	 * @param name
	 * @param filter
	 * @return
	 */
	public SecurityConfigurationSupport addFilter(String name, WebFilter filter) {
		if (filters == null) {
			filters = Maps.newHashMap();
		}
		filters.put(name, filter);
		return this;
	}
	
	/**
	 * 配置 FilterChain
	 * @param line
	 * @return
	 */
	public SecurityConfigurationSupport definitionFilterChain(String line) {
		if (!StringUtils.hasText(line)) {
			return this;
		}
		String[] parts = StringUtils.split(line, '=');
		if (!(parts != null && parts.length == 2)) {
			return this;
		}
		String path = StringUtils.clean(parts[0]);
		String filter = StringUtils.clean(parts[1]);
		if (!(StringUtils.hasText(path) && StringUtils.hasText(filter))) {
			return this;
		}
		if (chains == null) {
			chains = Maps.newHashMap();
		}
		chains.put(path, filter);
		return this;
	}
}