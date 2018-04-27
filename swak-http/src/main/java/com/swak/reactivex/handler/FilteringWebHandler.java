package com.swak.reactivex.handler;

import java.util.Arrays;
import java.util.List;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

import io.reactivex.Observable;

public class FilteringWebHandler extends WebHandlerDecorator{

	private final WebFilter[] filters;
	
	
	/**
	 * Constructor.
	 * @param filters the chain of filters
	 */
	public FilteringWebHandler(WebHandler webHandler, List<WebFilter> filters) {
		super(webHandler);
		this.filters = filters.toArray(new WebFilter[0]);
	}
	
	/**
	 * Return a read-only list of the configured filters.
	 */
	public List<WebFilter> getFilters() {
		return Arrays.asList(this.filters);
	}
	
	@Override
	public Observable<Void> handle(HttpServletRequest request, HttpServletResponse response) {
		return this.filters.length != 0 ?
				new DefaultWebFilterChain(getDelegate(), this.filters).filter(request, response) :
				super.handle(request, response);
	}
}
