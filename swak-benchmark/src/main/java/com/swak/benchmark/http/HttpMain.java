package com.swak.benchmark.http;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncCompletionHandlerBase;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
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

import com.swak.common.http.HttpClients;
import com.swak.common.http.reactor.ReactorHttpClient;

@Warmup(iterations = 3)
@OutputTimeUnit(TimeUnit.SECONDS)
@Measurement(iterations = 10, time = -1, timeUnit = TimeUnit.SECONDS, batchSize = 1)
@Threads(10)
@Fork(1)
public class HttpMain {

	@State(Scope.Benchmark)
	public static class BenchmarkState {
		public BenchmarkState() {
			AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
			HttpClients.setAsyncHttpClient(asyncHttpClient);
		}
	}
	
	@Benchmark
	public void common_get(BenchmarkState state) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		HttpClients.client().prepareGet("http://www.example.com/").execute(new AsyncCompletionHandler<Response>() {
			@Override
			public Response onCompleted(Response arg0) throws Exception {
				latch.countDown();
				return arg0;
			}
		});
		latch.await();
	}
	
	@Benchmark
	public void future_get(BenchmarkState state) {
		CompletableFuture<Response> promise = HttpClients.client()
	            .prepareGet("http://www.example.com/")
	            .execute()
	            .toCompletableFuture()
	            .exceptionally(t ->{ return null;});
	    promise.join(); // wait for completion
	}
	
	@Benchmark
	public void mono_get(BenchmarkState state) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		Request request = new RequestBuilder().setUrl("http://www.example.com/").build();
		ReactorHttpClient.create(HttpClients.client()).prepare(request, new AsyncCompletionHandlerBase()).subscribe((t)->{
			latch.countDown();
	    });
		latch.await();
	}
	
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(HttpMain.class.getSimpleName()).forks(1).build();
		new Runner(opt).run();
	}
}