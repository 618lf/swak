package com.tmt.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RxTest {

	public static void main(String[] args) throws InterruptedException {
		Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
			@Override
			public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
				emitter.onNext(1);
				emitter.onNext(2);
				emitter.onNext(3);
			}
		});
		
		Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
			@Override
			public void subscribe(ObservableEmitter<String> emitter) throws Exception {
				emitter.onNext("A");
				emitter.onNext("B");
				emitter.onNext("C");
				emitter.onNext("D");
			}
		});
		
		Observer<String> observer = new Observer<String>() {
			private Disposable mDisposable;
            private int i;
			@Override
			public void onSubscribe(Disposable d) {
				mDisposable = d;
				// System.out.println("sub:" + Thread.currentThread().getName());
				System.out.println("onSub");
			}

			@Override
			public void onNext(String t) {
				System.out.println(t);
				// System.out.println("next:" + Thread.currentThread().getName());
				i++;
				if (i > 4) {
					mDisposable.dispose();
				}
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("错误");
			}

			@Override
			public void onComplete() {
				System.out.println("完成");
			}
		};
		Observable.zip(observable1, observable2, (i, s) -> {
			return i + s;
		}).subscribe(observer);
	}
}
