package com.tmt.reactor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.tmt.reactor.b.PriceTick;
import com.tmt.reactor.b.SomeFeed;
import com.tmt.reactor.b.SomeListener;

import reactor.core.publisher.Mono;

public class MonoMain {
	
	/**
	 * 这是一个异步任务 -- 用于异步产生数据
	 * @param feed
	 */
	public static Future<Void> send(SomeFeed feed) {
		return CompletableFuture.runAsync(() ->{
			try {
				Thread.sleep(10000L);
				System.out.println("发送任务");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).thenAcceptAsync((t) ->{
			System.out.println("继续处理");
			feed.publish(new PriceTick("lifeng", false));
		});
	}
	
	/**
	 * 这是一个异步任务 -- 用于异步取消任务
	 * @param feed
	 */
	public static void monitor(Subscription s) {
		CompletableFuture.runAsync(() ->{
			try {
				Thread.sleep(2000L);
				System.out.println("monitor ： 任务超时");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			s.cancel();
		});
	}

	public static void main(String[] args) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		SomeFeed feed = new SomeFeed();
		// 10s 后发送数据
		final Future<Void> f = send(feed);
		Mono<PriceTick> foo = Mono.create(sink -> {
			SomeListener listener = new SomeListener() {
				@Override
				public void priceTick(PriceTick event) {
					sink.success(event);
				}
				@Override
				public void error(Throwable e) {
					sink.error(e);
				}
			};
			sink.onDispose(() ->{
				System.out.println("任务取消了");
				f.cancel(true);
			});
			feed.register(listener);
		});
		foo.subscribe(new Subscriber<PriceTick>() {
			@Override
			public void onSubscribe(Subscription s) {
				s.request(Long.MAX_VALUE);
				monitor(s);
			}
			@Override
			public void onNext(PriceTick t) {
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