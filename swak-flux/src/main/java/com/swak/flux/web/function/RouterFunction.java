package com.swak.flux.web.function;

import com.swak.flux.transport.http.server.HttpServerRequest;

import reactor.core.publisher.Mono;

public interface RouterFunction {

	/**
	 * Return the {@linkplain HandlerFunction handler function} that matches the given request.
	 * @param request the request to route
	 * @return an {@code Mono} describing the {@code HandlerFunction} that matches this request,
	 * or an empty {@code Mono} if there is no match
	 */
	HandlerFunction route(HttpServerRequest request);
	
	/**
	 * Return a composed routing function that first invokes this function,
	 * and then invokes the {@code other} function (of the same response type {@code T})
	 * if this route had {@linkplain Mono#empty() no result}.
	 * @param other the function of type {@code T} to apply when this function has no result
	 * @return a composed function that first routes with this function and then the
	 * {@code other} function if this function has no result
	 * @see #andOther(RouterFunction)
	 */
	default RouterFunction and(RouterFunction other) {
		return new RouterFunctions.ComposedRouterFunction(this, other);
	}
}
