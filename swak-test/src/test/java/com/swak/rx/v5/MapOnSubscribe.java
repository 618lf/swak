package com.swak.rx.v5;

/**
 * 中间的观察对象
 * 
 * @author lifeng
 * @date 2020年7月20日 下午3:38:11
 */
public class MapOnSubscribe<T, R> implements Observable.OnSubscribe<R> {
	final Observable<T> source;
	final Observable.Transformer<? super T, ? extends R> transformer;

	public MapOnSubscribe(Observable<T> source, Observable.Transformer<? super T, ? extends R> transformer) {
		this.source = source;
		this.transformer = transformer;
	}

	@Override
	public void call(Subscriber<? super R> subscriber) {
		source.subscribe(new MapSubscriber<R, T>(subscriber, transformer));
	}
}