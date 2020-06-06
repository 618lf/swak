package com.swak.fetty.transport.promise;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import com.swak.fetty.transport.channel.Channel;

/**
 * 异步结果
 * 
 * @author lifeng
 * @date 2020年6月4日 下午5:14:28
 */
public class DefaultPromise implements ChannelPromise {
	private static final AtomicReferenceFieldUpdater<DefaultPromise, Object> RESULT_UPDATER = AtomicReferenceFieldUpdater
			.newUpdater(DefaultPromise.class, Object.class, "result");
	private static final Object SUCCESS = new Object();
	private static final Object UNCANCELLABLE = new Object();
	private volatile Object result;

	/**
	 * 异步监听
	 */
	private Object listeners;

	/**
	 * 等待
	 */
	private short waiters;

	@Override
	public Channel channel() {
		return null;
	}

	@Override
	public ChannelPromise setSuccess(Object success) {
		return null;
	}

	@Override
	public ChannelPromise setFailure(Throwable cause) {
		return null;
	}

	@Override
	public ChannelPromise addListener(PromiseListener listener) {
		return null;
	}

	private boolean setValue0(Object objResult) {
		if (RESULT_UPDATER.compareAndSet(this, null, objResult)
				|| RESULT_UPDATER.compareAndSet(this, UNCANCELLABLE, objResult)) {
			if (checkNotifyWaiters()) {
				//notifyListeners();
			}
			return true;
		}
		return false;
	}

	private synchronized boolean checkNotifyWaiters() {
		if (waiters > 0) {
			notifyAll();
		}
		return listeners != null;
	}
}
