package com.swak.common.reactor.disposable;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import reactor.core.Disposable;

/**
 * like rxjava FutureDisposable
 * @author lifeng
 */
public class FutureDisposable extends AtomicReference<Future<?>> implements Disposable {

	private static final long serialVersionUID = 6545242830671168775L;

	private final boolean allowInterrupt;

	public FutureDisposable(Future<?> run, boolean allowInterrupt) {
		super(run);
		this.allowInterrupt = allowInterrupt;
	}

	@Override
	public boolean isDisposed() {
		Future<?> f = get();
		return f == null || f.isDone();
	}

	@Override
	public void dispose() {
		Future<?> f = getAndSet(null);
		if (f != null) {
			f.cancel(allowInterrupt);
		}
	}
}