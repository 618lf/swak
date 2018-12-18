package com.swak.http.builder;

import static org.asynchttpclient.util.HttpConstants.Methods.GET;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncCompletionHandlerBase;
import org.asynchttpclient.RequestBuilderBase;

import com.swak.http.HttpClients;
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

	private AsyncCompletionHandler<?> handler;
	
	// 基本构建
	public RequestBuilder() {
		this(GET);
	}

	public RequestBuilder(String method) {
		this(method, false);
	}

	protected RequestBuilder(String method, boolean disableUrlEncoding) {
		super(method, disableUrlEncoding);
	}
	
	// 响应数据
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
	
	@SuppressWarnings("unchecked")
	public <T> RequestBuilder charset(Charset charset) {
		if (handler != null && handler instanceof AbstractResponse) {
			((AbstractResponse<T>)handler).use(charset);
		}
		return this;
	}
	
	// 获得响应处理
	private AsyncCompletionHandler<?> getHandler() {
		return this.handler == null ? new AsyncCompletionHandlerBase() : this.handler;
	}
	public RequestBuilder handle(AsyncCompletionHandler<?> handler) {
		this.handler = handler;
		return this;
	}

	// 执行请求
	public <T> CompletableFuture<T> future(AsyncCompletionHandler<T> handler) {
		return HttpClients.future(build(), handler).toCompletableFuture();
	}
	@SuppressWarnings("unchecked")
	public <T> CompletableFuture<T> future() {
		return (CompletableFuture<T>) HttpClients.future(build(), getHandler()).toCompletableFuture();
	}
	public <T> Mono<T> reactive(AsyncCompletionHandler<T> handler) {
		return ReactorHttpClient.create(HttpClients.client()).prepare(build(), handler);
	}
	@SuppressWarnings("unchecked")
	public <T> Mono<T> reactive() {
		return (Mono<T>) ReactorHttpClient.create(HttpClients.client()).prepare(build(), getHandler());
	}

	/**
	 * 创建一个builder
	 * 
	 * @return
	 */
	public static RequestBuilder post() {
		return new RequestBuilder(HttpMethod.POST.name());
	}
	
	/**
	 * 创建一个builder
	 * 
	 * @return
	 */
	public static RequestBuilder get() {
		return new RequestBuilder(HttpMethod.GET.name());
	}
}
