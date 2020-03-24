package com.swak.http.builder;

import static org.asynchttpclient.util.HttpConstants.Methods.GET;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncCompletionHandlerBase;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.RequestBuilderBase;

import com.swak.http.handler.AbstractResponse;
import com.swak.http.handler.JsonResponse;
import com.swak.http.handler.PlainResponse;
import com.swak.http.handler.TextResponse;
import com.swak.http.handler.XmlResponse;
import com.swak.http.reactor.ReactorHttpClient;

import io.netty.handler.codec.http.HttpMethod;
import reactor.core.publisher.Mono;

/**
 * 构建请求，并执行
 * 
 * @author lifeng
 */
public class RequestBuilder extends RequestBuilderBase<RequestBuilder> {

	/**
	 * 响应处理器
	 */
	private AsyncCompletionHandler<?> handler;

	/**
	 * 使用的客户端
	 */
	private AsyncHttpClient client;

	// 基本构建
	public RequestBuilder(AsyncHttpClient client) {
		super(GET, false);
		this.client = client;
	}

	// ********* 请求方式 *************
	public RequestBuilder post() {
		this.setMethod(HttpMethod.POST.name());
		return this;
	}

	public RequestBuilder get() {
		this.setMethod(HttpMethod.GET.name());
		return this;
	}

	public RequestBuilder method(HttpMethod method) {
		this.setMethod(method.name());
		return this;
	}

	// ********* 响应处理方式 *************
	public <T> RequestBuilder json(Class<T> clazz) {
		handler = JsonResponse.create(clazz);
		return this;
	}

	public <T> RequestBuilder xml(Class<T> clazz) {
		handler = XmlResponse.create(clazz);
		return this;
	}

	public RequestBuilder text() {
		handler = TextResponse.create();
		return this;
	}

	public RequestBuilder plain() {
		handler = PlainResponse.create();
		return this;
	}

	public RequestBuilder handle(AsyncCompletionHandler<?> handler) {
		this.handler = handler;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> RequestBuilder charset(Charset charset) {
		if (handler != null && handler instanceof AbstractResponse) {
			((AbstractResponse<T>) handler).use(charset);
		}
		return this;
	}

	// 获得响应处理
	private AsyncCompletionHandler<?> getHandler() {
		return this.handler == null ? new AsyncCompletionHandlerBase() : this.handler;
	}

	// ********* 执行处理 *************
	public <T> CompletableFuture<T> future(AsyncCompletionHandler<T> handler) {
		return getClient().executeRequest(build(), handler).toCompletableFuture();
	}

	@SuppressWarnings("unchecked")
	public <T> CompletableFuture<T> future() {
		return (CompletableFuture<T>) getClient().executeRequest(build(), getHandler()).toCompletableFuture();
	}

	public <T> Mono<T> reactive(AsyncCompletionHandler<T> handler) {
		return ReactorHttpClient.create(getClient()).prepare(build(), handler);
	}

	@SuppressWarnings("unchecked")
	public <T> Mono<T> reactive() {
		return (Mono<T>) ReactorHttpClient.create(getClient()).prepare(build(), getHandler());
	}

	/**
	 * 默认使用设置的客户端
	 * 
	 * @return
	 */
	private AsyncHttpClient getClient() {
		return client;
	}

	/**
	 * 创建一个builder
	 * 
	 * @return
	 */
	public static RequestBuilder client(AsyncHttpClient client) {
		return new RequestBuilder(client);
	}
}
