package com.swak.rx.v4;

/**
 * 异步任务
 * 
 * @author lifeng
 * @date 2020年7月19日 下午10:50:33
 */
public interface AsynJob<T> {
	void then(CallBack<T> callback);

	default <R> AsynJob<R> map(final Func<T, AsynJob<R>> func) {
		final AsynJob<T> source = this;
		return new AsynJob<R>() {
			@Override
			public void then(final CallBack<R> callback) {
				source.then(new CallBack<T>() {
					@Override
					public void onSucess(T result) {
						AsynJob<R> mapped = func.call(result);
						mapped.then(new CallBack<R>() {

							@Override
							public void onSucess(R result) {
								callback.onSucess(result);
							}

							@Override
							public void onError(Throwable e) {
								callback.onError(e);
							}
						});
					}

					@Override
					public void onError(Throwable e) {
						callback.onError(e);
					}
				});
			}
		};
	}
}