package com.swak.reactivex.handler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

import io.reactivex.Observable;

public class DefaultWebFilterChain implements WebFilterChain{

	private final List<WebFilter> filters;
	private final WebHandler handler;
	private final int index;
	
	public DefaultWebFilterChain(WebHandler handler, WebFilter... filters) {
		Assert.notNull(handler, "WebHandler is required");
		this.filters = ObjectUtils.isEmpty(filters) ? Collections.emptyList() : Arrays.asList(filters);
		this.handler = handler;
		this.index = 0;
	}
	
	private DefaultWebFilterChain(DefaultWebFilterChain parent, int index) {
		this.filters = parent.getFilters();
		this.handler = parent.getHandler();
		this.index = index;
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
	public Observable<Void> filter(HttpServletRequest request, HttpServletResponse response) {
		return Observable.defer(() -> {
			if (this.index < this.filters.size()) {
				WebFilter filter = this.filters.get(this.index);
				WebFilterChain chain = new DefaultWebFilterChain(this, this.index + 1);
				return filter.filter(request, response, chain);
			} else {
				return this.handler.handle(request, response);
			}
		});
	}
}
