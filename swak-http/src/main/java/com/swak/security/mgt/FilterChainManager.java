package com.swak.security.mgt;

import java.util.Map;
import java.util.Set;

import com.swak.http.Filter;
import com.swak.http.FilterChain;

public interface FilterChainManager {

	 boolean hasChains();
	 
	 Set<String> getChainNames();
	 
	 FilterChain proxy(FilterChain original, String chainName);
	 
	 Map<String, Filter> getFilters();
	 
	 void addFilter(String name, Filter filter);
	 
	 void addFilter(String name, Filter filter, boolean init);
	 
	 void createChain(String chainName, String chainDefinition);
}
