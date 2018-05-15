package com.tmt.reactor;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import reactor.core.publisher.Mono;

public class ReactorMain {

	public static void main(String[] args) {

		Mono.just("123").map(s -> { /*int i= 1/0;*/ return s+"1";}).doOnSuccess(done -> {
			System.out.println("中间监听：" + done);
		}).doOnError(cause -> {
			System.out.println("中间监听：" + cause);
		}).doOnSuccessOrError((a,b) ->{
			System.out.println("都会收到消息");
		}).subscribe(new Subscriber<String>() {

			@Override
			public void onSubscribe(Subscription s) {
				s.request(Long.MAX_VALUE);
			}

			@Override
			public void onNext(String t) {
				System.out.println("收到消息" + t);
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("错误");
			}

			@Override
			public void onComplete() {
				System.out.println("完成");
			}
		});
	}
}
