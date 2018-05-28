package com.swak.http;

import java.util.concurrent.CompletableFuture;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncCompletionHandlerBase;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import com.swak.http.reactor.ReactorHttpClient;

import reactor.core.publisher.Mono;

public class HttpClients {

	// 需要http client 才能使用
	private static AsyncHttpClient httpClient = null;
	public static void setAsyncHttpClient(AsyncHttpClient httpClient) {
		HttpClients.httpClient = httpClient;
	}
	public static AsyncHttpClient client() {
		return httpClient;
	}
	
	/**
	 * 执行请求，并设置 handler
	 * handler 是用于将 Response -> 具体的对象
	 * @param request
	 * @param handler
	 * @return
	 */
	public static <T> CompletableFuture<T> future(Request request, AsyncCompletionHandler<T> handler) {
		return HttpClients.client().executeRequest(request, handler).toCompletableFuture();
	}
	
	/**
	 * 执行请求，返回 Response， 可以不处理 Response
	 * @param request
	 * @param handler
	 * @return
	 */
	public static CompletableFuture<Response> future(Request request) {
		return HttpClients.client().executeRequest(request, new AsyncCompletionHandlerBase()).toCompletableFuture();
	}
	
	/**
	 * 执行请求，返回 Response， 可以不处理 Response
	 * @param request
	 * @param handler
	 * @return
	 */
	public static <T> Mono<T> reactive(Request request, AsyncCompletionHandler<T> handler) {
		return ReactorHttpClient.create(httpClient).prepare(request, handler);
	}
	
	/**
	 * 执行请求，返回 Response， 可以不处理 Response
	 * @param request
	 * @param handler
	 * @return
	 */
	public static Mono<Response> reactive(Request request) {
		return ReactorHttpClient.create(httpClient).prepare(request, new AsyncCompletionHandlerBase());
	}
}