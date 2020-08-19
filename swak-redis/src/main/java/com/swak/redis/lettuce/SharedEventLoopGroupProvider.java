package com.swak.redis.lettuce;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.lettuce.core.resource.DefaultEventLoopGroupProvider;
import io.lettuce.core.resource.EventLoopGroupProvider;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * 单独针对lettuce的EventLoop, 不知道集群环境有啥问题
 * 
 * @see DefaultEventLoopGroupProvider
 * @author lifeng
 */
public class SharedEventLoopGroupProvider implements EventLoopGroupProvider {

	protected static final InternalLogger logger = InternalLoggerFactory
			.getInstance(SharedEventLoopGroupProvider.class);
	private final EventLoopGroup eventLoopGroup;
	private final AtomicLong refCounter = new AtomicLong(0);
	private volatile boolean shutdownCalled = false;
	private final int threadPoolSize;

	public SharedEventLoopGroupProvider(EventLoopGroup eventLoopGroup, int threadPoolSize) {
		this.eventLoopGroup = eventLoopGroup;
		this.threadPoolSize = threadPoolSize;
	}

	@Override
	public <T extends EventLoopGroup> T allocate(Class<T> type) {
		if (shutdownCalled) {
			throw new IllegalStateException("Provider is shut down and can not longer provide resources");
		}
		synchronized (this) {
			logger.debug("Allocating executor {}", type.getName());
			return addReference();
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends ExecutorService> T addReference() {
		Long counter = refCounter.getAndIncrement();
		logger.debug("Adding reference to {}, existing ref count {}", eventLoopGroup, counter);
		return (T) eventLoopGroup;
	}

	@Override
	public int threadPoolSize() {
		return threadPoolSize;
	}

	@Override
	public Future<Boolean> release(EventExecutorGroup eventLoopGroup, long quietPeriod, long timeout, TimeUnit unit) {
		logger.debug("Release executor {}", eventLoopGroup);

		if (this.eventLoopGroup == eventLoopGroup) {
			release();
		} else if (eventLoopGroup.isShuttingDown() || refCounter.get() != 0) {
			DefaultPromise<Boolean> promise = new DefaultPromise<Boolean>(GlobalEventExecutor.INSTANCE);
			promise.setSuccess(true);
			return promise;
		}

		Future<?> shutdownFuture = eventLoopGroup.shutdownGracefully(quietPeriod, timeout, unit);
		return toBooleanPromise(shutdownFuture);
	}

	@SuppressWarnings("unchecked")
	private <T extends ExecutorService> T release() {
		synchronized (refCounter) {
			long counter = refCounter.get();
			if (counter < 1) {
				logger.debug("Attempting to release {} but ref count is {}", eventLoopGroup, counter);
			}
			refCounter.decrementAndGet();
		}
		return (T) eventLoopGroup;
	}

	@Override
	public Future<Boolean> shutdown(long quietPeriod, long timeout, TimeUnit timeUnit) {
		logger.debug("Initiate shutdown ({}, {}, {})", quietPeriod, timeout, timeUnit);
		shutdownCalled = true;
		return release(eventLoopGroup, quietPeriod, timeout, timeUnit);
	}

	/**
	 * Create a promise that emits a {@code Boolean} value on completion of the
	 * {@code future}
	 *
	 * @param future
	 *            the future.
	 * @return Promise emitting a {@code Boolean} value. {@literal true} if the
	 *         {@code future} completed successfully, otherwise the cause wil be
	 *         transported.
	 */
	private Promise<Boolean> toBooleanPromise(Future<?> future) {

		DefaultPromise<Boolean> result = new DefaultPromise<>(GlobalEventExecutor.INSTANCE);

		if (future.isDone() || future.isCancelled()) {
			if (future.isSuccess()) {
				result.setSuccess(true);
			} else {
				result.setFailure(future.cause());
			}
			return result;
		}

		future.addListener((GenericFutureListener<Future<Object>>) f -> {

			if (f.isSuccess()) {
				result.setSuccess(true);
			} else {
				result.setFailure(f.cause());
			}
		});
		return result;
	}
}