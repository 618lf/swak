package com.swak.common.http.reactor.sink;

import static java.util.Objects.requireNonNull;

import org.asynchttpclient.handler.ProgressAsyncHandler;

import com.swak.common.reactor.publisher.DisposableMonoSink;

public class ProgressAsyncSinkEmitterBridge<T> extends AbstractSinkProgressAsyncHandlerBridge<T> {

	private final ProgressAsyncHandler<? extends T> delegate;

	public ProgressAsyncSinkEmitterBridge(DisposableMonoSink<T> emitter, ProgressAsyncHandler<? extends T> delegate) {
		super(emitter);
		this.delegate = requireNonNull(delegate);
	}

	@Override
	protected ProgressAsyncHandler<? extends T> delegate() {
		return delegate;
	}
}