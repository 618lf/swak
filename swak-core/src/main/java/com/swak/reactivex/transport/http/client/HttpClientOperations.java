package com.swak.reactivex.transport.http.client;

import java.util.function.BiFunction;

import com.swak.reactivex.transport.channel.ChannelOperations;
import com.swak.reactivex.transport.channel.ContextHandler;

import io.netty.channel.Channel;
import reactor.core.publisher.Mono;

/**
 * http client ops
 * 
 * @author lifeng
 */
public class HttpClientOperations extends ChannelOperations<HttpClientResponse, HttpClientRequest>
		implements HttpClientResponse, HttpClientRequest {

	protected HttpClientOperations(Channel channel,
			BiFunction<? super HttpClientResponse, ? super HttpClientRequest, ? extends Mono<Void>> handler,
			ContextHandler context) {
		super(channel, handler, context);
	}
	
	// ------------- 处理请求  ------------------
	/**
	 * 处理请求
	 */
	@Override
	protected void onHandlerStart() {
		
	}

	@Override
	public void onError(Throwable t) {

	}

	@Override
	public void onComplete() {

	}

	// --------------- 创建 ---------------------
	public static HttpClientOperations bind(Channel channel,
			BiFunction<? super HttpClientResponse, ? super HttpClientRequest, Mono<Void>> handler,
			ContextHandler context) {
		return new HttpClientOperations(channel, handler, context);
	}
}