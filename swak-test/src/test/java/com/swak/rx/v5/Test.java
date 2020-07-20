package com.swak.rx.v5;

import com.swak.rx.v5.Observable.OnSubscribe;
import com.swak.rx.v5.Observable.Transformer;

public class Test {

	/**
	 * 简单： <br>
	 * Observable OnSubscribe <br>
	 * Observable1.subscribe Subscriber -> <br>
	 * Subscriber.start、 onSubscribe.call、Subscriber.onNext<br>
	 * <br>
	 * 加一层map:<br> 
	 * Observable1 OnSubscribe1 Subscriber <br>
	 * Observable2 MapOnSubscribe2 MapSubscriber2 Observable1 <br>
	 * Observable2.subscribe Subscriber -><br>
	 * -> Subscriber.start、 MapOnSubscribe2.call <br>
	 * -> Observable1.subscribe MapSubscriber2 <br>
	 * -> MapSubscriber2.start、OnSubscribe1.call、MapSubscriber2.onNext、Subscriber.onNext <br>
	 * 
	 * @param <R>
	 * 
	 * 
	 * 
	 * @param args
	 */
	public static <R> void main(String[] args) {
		Observable.create(new OnSubscribe<Integer>() {
			@Override
			public void call(Subscriber<? super Integer> subscriber) {
				subscriber.onNext(1);
			}
		}).map(new Transformer<Integer, Integer>() {
			@Override
			public Integer call(Integer from) {
				return from * 10;
			}
		}).subscribe(new Subscriber<Integer>() {

			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable t) {

			}

			@Override
			public void onNext(Integer var1) {
				System.out.println(var1);
			}
		});
	}
}
