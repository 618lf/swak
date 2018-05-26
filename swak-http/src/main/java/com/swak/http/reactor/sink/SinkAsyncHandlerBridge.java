package com.swak.http.reactor.sink;

import static java.util.Objects.requireNonNull;

import org.asynchttpclient.AsyncHandler;

import com.swak.reactor.publisher.DisposableMonoSink;

public class SinkAsyncHandlerBridge<T> extends AbstractSinkAsyncHandlerBridge<T> {

	private final AsyncHandler<? extends T> delegate;

	public SinkAsyncHandlerBridge(DisposableMonoSink<T> emitter, AsyncHandler<? extends T> delegate) {
		super(emitter);
		this.delegate = requireNonNull(delegate);
	}

	@Override
	protected AsyncHandler<? extends T> delegate() {
		return delegate;
	}
}