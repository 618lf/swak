package com.swak.flux.security.filter;

import java.util.List;

import com.swak.flux.handler.WebFilter;
import com.swak.flux.handler.WebFilterChain;
import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.transport.server.HttpServerResponse;

import reactor.core.publisher.Mono;

public class ProxiedFilterChain implements WebFilterChain {

	private WebFilterChain orig;
	private List<WebFilter> filters;
	private int index = 0;

	public ProxiedFilterChain(WebFilterChain orig, List<WebFilter> filters) {
		if (orig == null) {
			throw new NullPointerException("original FilterChain cannot be null.");
		}
		this.orig = orig;
		this.filters = filters;
		this.index = 0;
	}

	@Override
	public Mono<Void> filter(HttpServerRequest request, HttpServerResponse response) {
		if (this.filters == null || this.filters.size() == this.index) {
			return this.orig.filter(request, response);
		} else {
			return this.filters.get(this.index++).filter(request, response, this);
		}
	}
}
