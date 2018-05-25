package com.swak.http.reactor;

import org.asynchttpclient.AsyncHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;

import reactor.core.publisher.Mono;

/**
 * use reactor 
 * @author lifeng
 *
 */
public interface ReactorHttpClient {
	
	/**
	 * create default http client
	 * @param httpClient
	 * @return
	 */
	public static ReactorHttpClient create(AsyncHttpClient httpClient) {
		return new DefaultReactorHttpClient(httpClient);
	}
	
	/**
	 * Prepares the given {@code request}. For each subscription to the returned
	 * {@code Maybe}, a new HTTP request will be executed and the results of
	 * {@code AsyncHandlers} obtained from {@code handlerSupplier} will be emitted.
	 *
	 * @param <T>
	 *            the result type produced by handlers produced by
	 *            {@code handlerSupplier} and emitted by the returned {@code Maybe}
	 *            instance
	 * @param request
	 *            the request that is to be executed
	 * @param handlerSupplier
	 *            supplies the desired {@code AsyncHandler} instances that are used
	 *            to produce results
	 * @return a {@code Maybe} that executes {@code request} upon subscription and
	 *         that emits the results produced by the supplied handers
	 * @throws NullPointerException
	 *             if at least one of the parameters is {@code null}
	 */
	<T> Mono<T> prepare(Request request, AsyncHandler<T> handler);
}