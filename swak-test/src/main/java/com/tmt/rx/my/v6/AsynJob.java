package com.tmt.rx.my.v6;

/**
 * 异步任务
 * @author lifeng
 * @param <T>
 */
public abstract class AsynJob<T> {

	/**
	 * 执行回调
	 * @param callBack
	 * @return
	 */
	public abstract T call(CallBack<T> callBack);
	
	
	/**
	 * map 将 AsynJob 进行转换
	 * @param func
	 * @return
	 */
	public <R> AsynJob<R> map(Func<T, AsynJob<R>> func) {
		AsynJob<T> source = this;
		return new AsynJob<R>() {
			@Override
			public R call(CallBack<R> callBack) {
				source.call(new CallBack<T>() {
					@Override
					public void onResult(T result) {
						func.call(result).call(new CallBack<R> () {
							@Override
							public void onResult(R result) {
								callBack.onResult(result);
							}
							@Override
							public void onError() {
								callBack.onError();
							}
						});
					}
					@Override
					public void onError() {
						callBack.onError();
					}
				});
				return null;
			}
		};
	}
}