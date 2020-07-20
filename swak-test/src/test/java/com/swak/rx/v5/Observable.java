package com.swak.rx.v5;

/**
 * 可被观察的对象
 * 
 * @author lifeng
 * @date 2020年7月20日 下午3:21:27
 */
public class Observable<T> {

	/**
	 * 挂载观察者
	 */
	private final OnSubscribe<T> onSubscribe;

	private Observable(OnSubscribe<T> onSubscribe) {
		this.onSubscribe = onSubscribe;
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
	 * 关联观察者
	 * 
	 * @param subscriber
	 */
	public void subscribe(Subscriber<? super T> subscriber) {
		subscriber.onStart();
		onSubscribe.call(subscriber);
	}

	/**
	 * 創建一個观察者
	 * 
	 * @param onSubscribe
	 * @return
	 */
	public static <T> Observable<T> create(OnSubscribe<T> onSubscribe) {
		return new Observable<T>(onSubscribe);
	}

	/**
	 * 挂载观察者
	 * 
	 * @author lifeng
	 * @date 2020年7月20日 下午3:24:27
	 */
	public interface OnSubscribe<T> {
		void call(Subscriber<? super T> subscriber);
	}

	/**
	 * 转换器
	 * 
	 * @author lifeng
	 * @date 2020年7月20日 下午3:24:50
	 */
	public interface Transformer<T, R> {
		R call(T from);
	}
}