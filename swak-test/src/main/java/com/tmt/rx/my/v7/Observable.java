package com.tmt.rx.my.v7;

/**
 * 订阅源
 * 
 * @author lifeng
 */
public class Observable<T> {

	final OnSubscribe<T> onSubscribe;

	private Observable(OnSubscribe<T> onSubscribe) {
		this.onSubscribe = onSubscribe;
	}

	/**
	 * 創建一個訂閱源
	 * 
	 * @param onSubscribe
	 * @return
	 */
	public static <T> Observable<T> create(OnSubscribe<T> onSubscribe) {
		return new Observable<T>(onSubscribe);
	}

	/**
	 * 转换
	 * 
	 * @param transformer
	 * @return
	 */
	public <R> Observable<R> map(Transformer<? super T, ? extends R> transformer) {
		return create(new MapOnSubscribe<T, R>(this, transformer));
	}

	/**
	 * 将事件的生产切换到新的线程中执行
	 * 
	 * @param scheduler
	 * @return
	 */
	public Observable<T> subscribeOn(Scheduler scheduler) {
		return Observable.create(new OnSubscribe<T>() {
			@Override
			public void call(Subscriber<? super T> subscriber) {
				subscriber.onStart();
				scheduler.createWorker().schedule(new Runnable() {
					@Override
					public void run() {
						Observable.this.onSubscribe.call(subscriber);
					}
				});
			}
		});
	}

	/**
	 * 作用于下层Subscriber的，
	 * @param scheduler
	 * @return
	 */
	public Observable<T> observeOn(Scheduler scheduler) {
		return Observable.create(new OnSubscribe<T>() {
			@Override
			public void call(Subscriber<? super T> subscriber) {
				subscriber.onStart();
				Scheduler.Worker worker = scheduler.createWorker();
				Observable.this.onSubscribe.call(new Subscriber<T>() {
					@Override
					public void onCompleted() {
						worker.schedule(new Runnable() {
							@Override
							public void run() {
								subscriber.onCompleted();
							}
						});
					}

					@Override
					public void onError(Throwable t) {
						worker.schedule(new Runnable() {
							@Override
							public void run() {
								subscriber.onError(t);
							}
						});
					}

					@Override
					public void onNext(T var1) {
						worker.schedule(new Runnable() {
							@Override
							public void run() {
								subscriber.onNext(var1);
							}
						});
					}
				});
			}
		});
	}

	/**
	 * 关联观察者
	 * 
	 * @param subscriber
	 */
	public void subscribe(Subscriber<? super T> subscriber) {
		subscriber.onStart();
		onSubscribe.call(subscriber);
	}

	public interface OnSubscribe<T> {
		void call(Subscriber<? super T> subscriber);
	}

	public interface Transformer<T, R> {
		R call(T from);
	}
}