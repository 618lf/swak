package com.swak.http.reactor.sink;

import org.asynchttpclient.handler.ProgressAsyncHandler;

import com.swak.reactor.publisher.DisposableMonoSink;

public abstract class AbstractSinkProgressAsyncHandlerBridge<T> extends AbstractSinkAsyncHandlerBridge<T>
		implements ProgressAsyncHandler<Void> {

	protected AbstractSinkProgressAsyncHandlerBridge(DisposableMonoSink<T> emitter) {
		super(emitter);
	}

	@Override
	public final State onHeadersWritten() {
		return emitter.isDisposed() ? disposed() : delegate().onHeadersWritten();
	}

	@Override
	public final State onContentWritten() {
		return emitter.isDisposed() ? disposed() : delegate().onContentWritten();
	}

	@Override
	public final State onContentWriteProgress(long amount, long current, long total) {
		return emitter.isDisposed() ? disposed() : delegate().onContentWriteProgress(amount, current, total);
	}

	@Override
	protected abstract ProgressAsyncHandler<? extends T> delegate();

}