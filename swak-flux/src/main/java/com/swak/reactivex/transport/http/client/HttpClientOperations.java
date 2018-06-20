package com.swak.reactivex.transport.http.client;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import com.swak.reactivex.transport.NettyContext;
import com.swak.reactivex.transport.channel.ChannelOperations;
import com.swak.reactivex.transport.channel.ContextHandler;
import com.swak.reactor.publisher.FutureMono;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import reactor.core.publisher.Mono;

/**
 * http client ops
 * 不支持 keeyAlive
 * 
 * @author lifeng
 */
public class HttpClientOperations extends ChannelOperations<HttpClientResponse, HttpClientRequest>
		implements HttpClientResponse, HttpClientRequest, NettyContext {

	private CompletableFuture<HttpClientResponse> resultFuture;
	
	protected HttpClientOperations(Channel channel,
			BiFunction<? super HttpClientResponse, ? super HttpClientRequest, ? extends Mono<Void>> handler,
			ContextHandler context) {
		super(channel, handler, context);
		resultFuture = new CompletableFuture<HttpClientResponse>();
	}
	
	// ------------- 处理请求  ------------------
	/**
	 * 发送数据
	 */
	@Override
	public void onHandlerStart() {
		try {
			this.handler.apply(this, this).subscribe(this);
		} catch (Exception e) {
			this.onError(e);
		}
	}
	
	/**
	 * 发送数据出现错误
	 */
	@Override
	public void onError(Throwable t) {
		context().fireContextError(t);
	}

	/**
	 * 发送数据成功, 设置为第一步： 提交数据成功
	 * 这个时候还没真实的收到数据
	 */
	@Override
	public void onComplete() {
		context().fireContextActive(this);
	}

	/**
	 * channel 被关闭连接
	 */
	public void onChannelClose() {
		HttpClientOperations.remove(channel());
		if (!resultFuture.isCompletedExceptionally()
				&& !resultFuture.isDone()
				&& resultFuture.isCancelled()) {
			resultFuture.cancel(true);
		}
	}
	
	/**
	 * 设置为第二步： 成功获取到数据
	 * @param err
	 */
	public void onChannelRead(Object message) {
		// 设置结果数据
		
		// 将结果future 设置为成功
		if (!resultFuture.isCompletedExceptionally()
				&& !resultFuture.isDone()
				&& resultFuture.isCancelled()) {
			resultFuture.complete(this);
		}
	}
	
	/**
	 * channel 出现了异常
	 * @param err
	 */
	public void onChannelError(Throwable err) {
		HttpClientOperations.remove(channel());
		if (!resultFuture.isCompletedExceptionally()
				&& !resultFuture.isDone()
				&& resultFuture.isCancelled()) {
			resultFuture.completeExceptionally(err);
		}
	}
	
	// --------------- 发送请求数据 --------------
	
	/**
	 * 发送数据
	 * @return
	 */
	public Mono<Void> send() {
		return FutureMono.from(channel().writeAndFlush(this));
	}
	
	/**
	 * 异步结果
	 * @return
	 */
	public CompletableFuture<HttpClientResponse> getFuture() {
		return this.resultFuture;
	}

	// --------------- 创建 ---------------------
	public static HttpClientOperations bind(Channel channel,
			BiFunction<? super HttpClientResponse, ? super HttpClientRequest, Mono<Void>> handler,
			ContextHandler context) {
		return new HttpClientOperations(channel, handler, context);
	}
	
	// 客户端需要这个
	// http 协议一个 channel 同时只能处理一个请求，即使是keepalive的，必须等这个请求处理完成后在处理下一个，
	// 所以用这个来记录 request 的生命周期，channel 异常关闭时，正确的释放资源
	protected static final AttributeKey<HttpClientOperations> OPERATIONS_KEY = AttributeKey
			.newInstance("nettyOperations");
	public static HttpClientOperations get(Channel ch) {
		return ch.attr(OPERATIONS_KEY).get();
	}
	public static void remove(Channel ch) {
		ch.attr(OPERATIONS_KEY).set(null);
	}
	public static HttpClientOperations tryGetAndSet(Channel ch, HttpClientOperations ops) {
		Attribute<HttpClientOperations> attr = ch.attr(OPERATIONS_KEY);
		for (;;) {
			HttpClientOperations op = attr.get();
			if (op != null) {
				return op;
			}

			if (attr.compareAndSet(null, ops)) {
				return null;
			}
		}
	}
}