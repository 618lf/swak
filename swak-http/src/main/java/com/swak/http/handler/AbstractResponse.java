package com.swak.http.handler;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Request;

import com.swak.http.HttpClients;

import reactor.core.publisher.Mono;

/**
 * 基本的返回
 * @author lifeng
 * @param <T>	
 */
public abstract class AbstractResponse<T> extends AsyncCompletionHandler<T>{

	protected Charset charset;
	protected Class<T> clazz;
	public <U extends AbstractResponse<T>> U use(Charset charset) {
		this.charset = charset;
		return as();
	}
	public <U extends AbstractResponse<T>> U use(Class<T> clazz) {
		this.clazz = clazz;
		return as();
	}
	
	@SuppressWarnings("unchecked")
	protected <U extends AbstractResponse<T>> U as() {
		return (U) this;
	}
	
	/**
	 * 返回 future
	 * @param request
	 * @return
	 */
	public CompletableFuture<T> future(Request request) {
		return HttpClients.future(request, as());
	}
	
	/**
	 * 返回 reactive
	 * @param request
	 * @return
	 */
	public Mono<T> reactive(Request request) {
		return HttpClients.reactive(request, as());
	}
}