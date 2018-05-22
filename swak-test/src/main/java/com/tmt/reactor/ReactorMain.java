package com.tmt.reactor;

import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

public class ReactorMain {

	public static void async_task(MonoSink<Object> sink) {
		Thread nThread = new Thread() {
			@Override
			public void run() {
				try {
					System.out.println(Thread.currentThread().getName());
					Thread.sleep(2000L);
					sink.success("123");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		nThread.setDaemon(true);
		nThread.start();
	}

	public static void main(String[] args) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		//Mono.just("123").map(s -> Integer.parseInt(s)).defer(supplier)
		Mono.just("123").map(s -> Integer.parseInt(s)).flatMap((s) -> {
			return Mono.create((sink) -> {
				async_task(sink);
			}).flatMap((i) -> {
				return Mono.empty();
			});
		}).subscribe(new Subscriber() {
			@Override
			public void onSubscribe(Subscription s) {
			}
			@Override
			public void onNext(Object t) {
				System.out.println(t);
			}
			@Override
			public void onError(Throwable t) {
				
			}
			@Override
			public void onComplete() {
				latch.countDown();
				System.out.println("123");
			}
		});
		latch.await();
	}
}