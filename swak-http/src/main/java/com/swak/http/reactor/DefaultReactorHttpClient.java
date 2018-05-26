package com.swak.http.reactor;

import java.util.concurrent.Future;

import org.asynchttpclient.AsyncHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.handler.ProgressAsyncHandler;

import com.swak.http.reactor.sink.ProgressAsyncSinkEmitterBridge;
import com.swak.http.reactor.sink.SinkAsyncHandlerBridge;
import com.swak.reactor.disposable.FutureDisposable;
import com.swak.reactor.publisher.DisposableMonoSink;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

/**
 * 响应式的 http 客户端
 * 
 * @author lifeng
 */
public class DefaultReactorHttpClient implements ReactorHttpClient {

	private final AsyncHttpClient httpClient;

	DefaultReactorHttpClient(AsyncHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	/**
	 * disposable 主要监控 是否取消， 取消之后 AsyncHandler 怎么应对
	 * onDispose 是一个回调， 会通过 disposable 来 取消 disposable 中需要取消的资源
	 * -- 上层不会取消, 做一个简单的监控
	 */
	@Override
	public <T> Mono<T> prepare(Request request, AsyncHandler<T> handler) {
		return Mono.create(sink -> {
			DisposableMonoSink<T> sinkProxy = new DisposableMonoSink<T>(sink);
			final AsyncHandler<?> bridge = createBridge(sinkProxy, handler);
			final Future<?> responseFuture = httpClient.executeRequest(request, bridge);
			Disposable disposable = new FutureDisposable(responseFuture, true);
			sinkProxy.onDispose(disposable);
		});
	}

	/**
	 * Creates an {@code AsyncHandler} that bridges events from the given
	 * {@code handler} to the given {@code emitter} and cancellation/disposal in the
	 * other direction.
	 *
	 * @param <T>
	 *            the result type produced by {@code handler} and emitted by
	 *            {@code emitter}
	 * @param emitter
	 *            the RxJava emitter instance that receives results upon completion
	 *            and will be queried for disposal during event processing
	 * @param handler
	 *            the {@code AsyncHandler} instance that receives downstream events
	 *            and produces the result that will be emitted upon request
	 *            completion
	 * @return the bridge handler
	 */
	protected <T> AsyncHandler<?> createBridge(DisposableMonoSink<T> emitter, AsyncHandler<T> handler) {
		if (handler instanceof ProgressAsyncHandler) {
			return new ProgressAsyncSinkEmitterBridge<>(emitter, (ProgressAsyncHandler<? extends T>) handler);
		}

		return new SinkAsyncHandlerBridge<>(emitter, handler);
	}
}
