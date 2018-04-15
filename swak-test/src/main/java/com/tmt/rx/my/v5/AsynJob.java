package com.tmt.rx.my.v5;

import com.tmt.rx.my.v3.CallBack;

/**
 * 异步任务
 * @author lifeng
 * @param <T>
 */
public abstract class AsynJob<T> {
	
	public abstract void start(CallBack<T> callback);

	/**
	 * map 是转换的意思
	 * @param func
	 * @return
	 */
	public <R> AsynJob<R> map(final Func<T, AsynJob<R>> func) {
		final AsynJob<T> source = this;
		return new AsynJob<R>() {
			@Override
			public void start(final CallBack<R> callback) {
				source.start(new CallBack<T>() {
					@Override
					public void onResult(T result) {
						AsynJob<R> mapped = func.call(result);
						mapped.start(new CallBack<R>() {

							@Override
							public void onResult(R result) {
								callback.onResult(result);
							}

							@Override
							public void onError() {
								callback.onError();
							}
						});
					}

					@Override
					public void onError() {
						callback.onError();
					}
				});
			}
		};
	}
}
