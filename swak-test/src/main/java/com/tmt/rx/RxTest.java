package com.tmt.rx;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class RxTest {

	public static void main(String[] args) {
		Observable.unsafeCreate(new Observable.OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> t) {
				t.onNext("123");
			}
		}).observeOn(Schedulers.newThread()).subscribeOn(Schedulers.newThread()).subscribe();
	}
}
