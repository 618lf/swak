package com.tmt.rx.my.v7;

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
