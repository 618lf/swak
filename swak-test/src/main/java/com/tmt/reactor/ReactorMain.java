package com.tmt.reactor;

import java.util.concurrent.CountDownLatch;

import com.tmt.reactor.b.PriceTick;
import com.tmt.reactor.b.SomeFeed;
import com.tmt.reactor.b.SomeListener;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

public class ReactorMain {

	public static void main(String[] args) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		SomeFeed feed = new SomeFeed();
		Flux<PriceTick> foo = Flux.create((sink) -> {
			SomeListener listener = new SomeListener() {
				@Override
				public void priceTick(PriceTick event) {
					sink.next(event);
					if (event.isLast()) {
						sink.complete();
					}
				}
				@Override
				public void error(Throwable e) {
					sink.error(e);
				}
			};
			feed.register(listener);
		});
		
		// 直接订阅
//		foo.subscribe(new Subscriber<PriceTick>() {
//			@Override
//			public void onSubscribe(Subscription s) {
//				s.request(Long.MAX_VALUE);
//				System.out.println("onSubscribe");
//			}
//
//			@Override
//			public void onNext(PriceTick t) {
//				System.out.println("onNext :" + t);
//			}
//
//			@Override
//			public void onError(Throwable t) {
//				System.out.println("onError :" + t);
//			}
//
//			@Override
//			public void onComplete() {
//				latch.countDown();
//				System.out.println("onComplete :");
//			}
//		});
		ConnectableFlux<PriceTick> hot = foo.publish();
		hot.subscribe(event -> System.out.println("one -> " + event));
		hot.subscribe(event -> System.out.println("two -> " + event));
		hot.connect();
		
		feed.publish(new PriceTick("lifeng", false)).publish(new PriceTick("hanqian", true));
		latch.await();
	}
}