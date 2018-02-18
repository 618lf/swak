package com.swak.security.mgt.support;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.swak.common.utils.StringUtils;
import com.swak.http.Filter;
import com.swak.http.FilterChain;
import com.swak.security.exception.ConfigurationException;
import com.swak.security.filter.Nameable;
import com.swak.security.filter.NamedFilterList;
import com.swak.security.filter.PathConfigProcessor;
import com.swak.security.filter.SimpleNamedFilterList;
import com.swak.security.mgt.DefaultFilter;
import com.swak.security.mgt.FilterChainManager;

public class DefaultFilterChainManager implements FilterChainManager {

	private Map<String, Filter> filters; 
	private Map<String, NamedFilterList> filterChains; 

	public DefaultFilterChainManager() {
		this.filters = new LinkedHashMap<String, Filter>();
		this.filterChains = new LinkedHashMap<String, NamedFilterList>();
		this.addDefaultFilters(false);
	}

	public Map<String, Filter> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, Filter> filters) {
		this.filters = filters;
	}

	public Map<String, NamedFilterList> getFilterChains() {
		return filterChains;
	}

	public void setFilterChains(Map<String, NamedFilterList> filterChains) {
		this.filterChains = filterChains;
	}

	public Filter getFilter(String name) {
		return this.filters.get(name);
	}

	public void addFilter(String name, Filter filter) {
		addFilter(name, filter, false);
	}

	public void addFilter(String name, Filter filter, boolean init) {
		addFilter(name, filter, init, true);
	}

	public void createChain(String chainName, String chainDefinition) {
		if (!StringUtils.hasText(chainName)) {
			throw new NullPointerException("chainName cannot be null or empty.");
		}
		if (!StringUtils.hasText(chainDefinition)) {
			throw new NullPointerException(
					"chainDefinition cannot be null or empty.");
		}

		String[] filterTokens = splitChainDefinition(chainDefinition);
		for (String token : filterTokens) {
			String[] nameConfigPair = toNameConfigPair(token);
			addToChain(chainName, nameConfigPair[0], nameConfigPair[1]);
		}
	}

	protected String[] splitChainDefinition(String chainDefinition) {
		return StringUtils.split(chainDefinition, StringUtils.DEFAULT_DELIMITER_CHAR, '[', ']', true, true);
	}

	protected String[] toNameConfigPair(String token)
			throws ConfigurationException {

		try {
			String[] pair = token.split("\\[", 2);
			String name = StringUtils.clean(pair[0]);

			if (name == null) {
				throw new IllegalArgumentException(
						"Filter name not found for filter chain definition token: "
								+ token);
			}
			String config = null;

			if (pair.length == 2) {
				config = StringUtils.clean(pair[1]);
				config = config.substring(0, config.length() - 1);
				config = StringUtils.clean(config);

				if (config != null && config.startsWith("\"")
						&& config.endsWith("\"")) {
					String stripped = config.substring(1, config.length() - 1);
					stripped = StringUtils.clean(stripped);

					if (stripped != null && stripped.indexOf('"') == -1) {
						config = stripped;
					}
				}
			}

			return new String[] { name, config };

		} catch (Exception e) {
			String msg = "Unable to parse filter chain definition token: "
					+ token;
			throw new ConfigurationException(msg, e);
		}
	}

	protected void addFilter(String name, Filter filter, boolean init,
			boolean overwrite) {
		Filter existing = getFilter(name);
		if (existing == null || overwrite) {
			if (filter instanceof Nameable) {
				((Nameable) filter).setName(name);
			}
			this.filters.put(name, filter);
		}
	}

	public void addToChain(String chainName, String filterName) {
		addToChain(chainName, filterName, null);
	}

	public void addToChain(String chainName, String filterName,
			String chainSpecificFilterConfig) {
		if (!StringUtils.hasText(chainName)) {
			throw new IllegalArgumentException(
					"chainName cannot be null or empty.");
		}
		Filter filter = getFilter(filterName);
		if (filter == null) {
			throw new IllegalArgumentException(
					"There is no filter with name '"
							+ filterName
							+ "' to apply to chain ["
							+ chainName
							+ "] in the pool of available Filters.  Ensure a "
							+ "filter with that name/path has first been registered with the addFilter method(s).");
		}

		applyChainConfig(chainName, filter, chainSpecificFilterConfig);

		NamedFilterList chain = ensureChain(chainName);
		chain.add(filter);
	}

	protected void applyChainConfig(String chainName, Filter filter,
			String chainSpecificFilterConfig) {
		if (filter instanceof PathConfigProcessor) {
			((PathConfigProcessor) filter).processPathConfig(chainName,
					chainSpecificFilterConfig);
		} else {
			if (StringUtils.hasText(chainSpecificFilterConfig)) {
				String msg = "chainSpecificFilterConfig was specified, but the underlying "
						+ "Filter instance is not an 'instanceof' "
						+ PathConfigProcessor.class.getName()
						+ ".  This is required if the filter is to accept "
						+ "chain-specific configuration.";
				throw new ConfigurationException(msg);
			}
		}
	}

	protected NamedFilterList ensureChain(String chainName) {
		NamedFilterList chain = getChain(chainName);
		if (chain == null) {
			chain = new SimpleNamedFilterList(chainName);
			this.filterChains.put(chainName, chain);
		}
		return chain;
	}

	public NamedFilterList getChain(String chainName) {
		return this.filterChains.get(chainName);
	}

	public boolean hasChains() {
		return !this.filterChains.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public Set<String> getChainNames() {
		return this.filterChains != null ? this.filterChains.keySet() : Collections.EMPTY_SET;
	}

	public FilterChain proxy(FilterChain original, String chainName) {
		NamedFilterList configured = getChain(chainName);
		if (configured == null) {
			String msg = "There is no configured chain under the name/key ["
					+ chainName + "].";
			throw new IllegalArgumentException(msg);
		}
		return configured.proxy(original);
	}

	protected void addDefaultFilters(boolean init) {
		try {
			for (DefaultFilter defaultFilter : DefaultFilter.values()) {
	            addFilter(defaultFilter.name(), defaultFilter.newInstance(), init, false);
	        }
		} catch(Exception e) {}
    }
}