package com.tmt.reactor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import reactor.core.publisher.Mono;

public class MonoFuture {

	public static void main(String[] args) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		CompletableFuture<Void> future = CompletableFuture.runAsync(() ->{
			try {
				Thread.sleep(1000L);
				System.out.println("发送任务");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).thenAcceptAsync((t) ->{
			System.out.println("继续处理");
		});
		
		// 稍后在处理
		Thread.sleep(2000L);
		
		Mono<Void> m = Mono.fromFuture(future);
		m.subscribe(new Subscriber<Void>() {
			@Override
			public void onSubscribe(Subscription s) {
				s.request(Long.MAX_VALUE);
				System.out.println("onSubscribe");
			}

			@Override
			public void onNext(Void t) {
				System.out.println("onNext");
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("onError");
			}

			@Override
			public void onComplete() {
				System.out.println("onComplete");
			}
		});
		
		latch.await();
	}
}