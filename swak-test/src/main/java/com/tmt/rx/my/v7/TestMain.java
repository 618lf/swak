package com.tmt.rx.my.v7;

public class TestMain {

	public static void main(String[] args) {
		Observable.create(new Observable.OnSubscribe<Integer>() {
			@Override
			public void call(Subscriber<? super Integer> subscriber) {// onsub
				for (int i = 0; i < 10; i++) {
					subscriber.onNext(i);
				}
			}
		}).map(new Observable.Transformer<Integer, String>() { // onSub  suber1 订阅上层的 Observable
			@Override
			public String call(Integer from) {
				return "maping " + from;
			}
		}).subscribe(new Subscriber<String>() { // 入口 suber0
			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable t) {

			}

			@Override
			public void onNext(String var1) {
				System.out.println("收到数据：" + var1);
			}
		});
	}
}
