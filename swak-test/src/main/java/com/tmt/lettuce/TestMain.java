package com.tmt.lettuce;

import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.swak.common.cache.SafeEncoder;
import com.swak.common.cache.redis.RedisUtils;
import com.swak.common.cache.redis.factory.RedisClientDecorator;
import com.swak.common.cache.redis.factory.RedisConnectionFactory;
import com.swak.common.cache.redis.factory.RedisConnectionPoolFactory;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import reactor.core.publisher.Mono;

public class TestMain {

	public static void main(String[] args) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		RedisURI config = RedisURI.Builder.redis("localhost", 6379).withPassword("12345678....").build();
		RedisClient client = RedisClient.create(config);
		RedisClientDecorator decorator = new RedisClientDecorator(client);
		RedisConnectionFactory<byte[], byte[]> factory = new RedisConnectionPoolFactory(decorator);
		RedisUtils.setRedisConnectionFactory(factory);
		RedisFuture<byte[]> f = RedisUtils.async((commands) -> {
			return commands.get(SafeEncoder.encode("lifeng"));
		});
		Mono.fromCompletionStage(f).map(bs -> SafeEncoder.encode(bs)).subscribe(new Subscriber<String>() {
			@Override
			public void onSubscribe(Subscription s) {
				s.request(Long.MAX_VALUE);
				System.out.println("onSubscribe");
			}
			@Override
			public void onNext(String t) {
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