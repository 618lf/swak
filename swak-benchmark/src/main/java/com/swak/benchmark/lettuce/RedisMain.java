package com.swak.benchmark.lettuce;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.swak.common.cache.SafeEncoder;
import com.swak.common.cache.redis.RedisUtils;
import com.swak.common.cache.redis.factory.RedisClientDecorator;
import com.swak.common.cache.redis.factory.RedisConnectionFactory;
import com.swak.common.cache.redis.factory.RedisConnectionPoolFactory;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;

@Warmup(iterations = 3)
@OutputTimeUnit(TimeUnit.SECONDS)
@Measurement(iterations = 10, time = -1, timeUnit = TimeUnit.SECONDS, batchSize = 1)
@Threads(4)
@Fork(1)
public class RedisMain {
	
	@State(Scope.Benchmark)
	public static class BenchmarkState {
		public BenchmarkState() {
			RedisURI config = RedisURI.Builder.redis("localhost", 6379).withPassword("12345678....").build();
			RedisClient client = RedisClient.create(config);
			RedisClientDecorator decorator = new RedisClientDecorator(client);
			RedisConnectionFactory<byte[], byte[]> factory = new RedisConnectionPoolFactory(decorator);
			RedisUtils.setRedisConnectionFactory(factory);
		}
	}
	
	@Benchmark
	public void sync_get(BenchmarkState state) {
		RedisUtils.get("lifeng");
	}
	
	@Benchmark
	public void sync_set(BenchmarkState state) {
		RedisUtils.set("lifeng", SafeEncoder.encode("123"));
	}
	
	@Benchmark
	public void async_get(BenchmarkState state) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		RedisUtils.async((commands) -> commands.get(SafeEncoder.encode("lifeng"))).whenComplete((a, b)->{
			latch.countDown();
		});
		latch.await();
	}
	
	@Benchmark
	public void async_set(BenchmarkState state) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		RedisUtils.async((commands) -> commands.set(SafeEncoder.encode("lifeng"), SafeEncoder.encode("123"))).whenComplete((a, b)->{
			latch.countDown();
		});
		latch.await();
	}
	
	@Benchmark
	public void reactive_get(BenchmarkState state) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		RedisUtils.reactive((commands) -> commands.get(SafeEncoder.encode("lifeng"))).subscribe(new Subscriber<byte[]>() {
			@Override
			public void onSubscribe(Subscription s) {
				s.request(Long.MAX_VALUE);
			}
			@Override
			public void onNext(byte[] t) {
			}
			@Override
			public void onError(Throwable t) {
			}
			@Override
			public void onComplete() {
				latch.countDown();
			}
		});
		latch.await();
	}
	
	@Benchmark
	public void reactive_set(BenchmarkState state) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		RedisUtils.reactive((commands) -> commands.set(SafeEncoder.encode("lifeng"), SafeEncoder.encode("123"))).subscribe(new Subscriber<String>() {
			@Override
			public void onSubscribe(Subscription s) {
				s.request(Long.MAX_VALUE);
			}
			@Override
			public void onNext(String t) {
			}
			@Override
			public void onError(Throwable t) {
			}
			@Override
			public void onComplete() {
				latch.countDown();
			}
		});
		latch.await();
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(RedisMain.class.getSimpleName()).forks(1).build();
		new Runner(opt).run();
	}
}
