package com.swak.common.reactor.publisher;

import java.util.Objects;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;

/**
 * Convert Netty Future into void {@link Mono}.
 *
 * @author Stephane Maldini
 */
public abstract class FutureMono extends Mono<Void> {

	/**
	 * Convert a {@link Future} into {@link Mono}. {@link Mono#subscribe(Subscriber)}
	 * will bridge to {@link Future#addListener(GenericFutureListener)}.
	 *
	 * @param future the future to convert from
	 * @param <F> the future type
	 *
	 * @return A {@link Mono} forwarding {@link Future} success or failure
	 */
	public static <F extends Future<Void>> Mono<Void> from(F future) {
		if(future.isDone()){
			if(!future.isSuccess()){
				return Mono.error(future.cause());
			}
			return Mono.empty();
		}
		return new ImmediateFutureMono<>(future);
	}

	final static class ImmediateFutureMono<F extends Future<Void>> extends FutureMono {

		final F future;

		ImmediateFutureMono(F future) {
			this.future = Objects.requireNonNull(future, "future");
		}

		@Override
		public final void subscribe(final CoreSubscriber<? super Void> s) {
			if(future.isDone()){
				if(future.isSuccess()){
					Operators.complete(s);
				}
				else{
					Operators.error(s, future.cause());
				}
				return;
			}

			FutureSubscription<F> fs = new FutureSubscription<>(future, s);
			s.onSubscribe(fs);
			future.addListener(fs);
		}
	}

	final static class FutureSubscription<F extends Future<Void>> implements
	                                                GenericFutureListener<F>,
	                                                Subscription {

		final CoreSubscriber<? super Void> s;
		final F                        future;

		FutureSubscription(F future, CoreSubscriber<? super Void> s) {
			this.s = s;
			this.future = future;
		}

		@Override
		public void request(long n) {
			//noop
		}

		@Override
		public void cancel() {
			future.removeListener(this);
		}

		@Override
		public void operationComplete(F future) throws Exception {
			if (!future.isSuccess()) {
				s.onError(future.cause());
			}
			else {
				s.onComplete();
			}
		}
	}
}