package com.swak.http;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig.Builder;

import com.swak.http.HttpClients;
import com.swak.http.builder.RequestBuilder;


public class TestNettyClient {
	
	public static void https() {
		
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		Builder builder = new DefaultAsyncHttpClientConfig.Builder();
		
		// 超时时间
		builder.setConnectTimeout(300000);
		builder.setRequestTimeout(300000);
		builder.setReadTimeout(300000);
		builder.setHandshakeTimeout(300000);
		
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient(builder.build());
		HttpClients.setAsyncHttpClient(asyncHttpClient);
		CountDownLatch latch = new CountDownLatch(1);
		AtomicInteger count = new AtomicInteger();
		for(int i=0; i< 600; i++) {
			RequestBuilder.get().setUrl("https://www.cnblogs.com/guogangj/p/5462594.html")
			.text().future().whenComplete((v,t) -> {
				count.incrementAndGet();
				if (count.get() == 600) {
					latch.countDown();
				}
				if (t != null) {
					System.out.println(t);
				}
			});
		}
		latch.await();
		System.out.println("完成请求:" + count.get());
		asyncHttpClient.close();
	}
}