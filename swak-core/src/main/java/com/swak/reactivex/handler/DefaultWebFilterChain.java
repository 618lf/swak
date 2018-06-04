package com.swak.reactivex.handler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.swak.reactivex.transport.http.HttpServerRequest;
import com.swak.reactivex.transport.http.HttpServerResponse;

import reactor.core.publisher.Mono;

public class DefaultWebFilterChain implements WebFilterChain {

	private final List<WebFilter> filters;
	private final WebHandler handler;
	private int index;
	
	public DefaultWebFilterChain(WebHandler handler, WebFilter... filters) {
		Assert.notNull(handler, "WebHandler is required");
		this.filters = ObjectUtils.isEmpty(filters) ? Collections.emptyList() : Arrays.asList(filters);
		this.handler = handler;
		this.index = 0;
	}
	
	public List<WebFilter> getFilters() {
		return this.filters;
	}

	public WebHandler getHandler() {
		return this.handler;
	}
	
	/**
	 * 链式的添加 Observable
	 */
	@Override
	public Mono<Void> filter(HttpServerRequest request, HttpServerResponse response) {
		if (this.index < this.filters.size()) {
			return this.filters.get(this.index++).filter(request, response, this);
		} else {
			return this.handler.handle(request, response);
		}
	}
}