package com.tmt.httpclient;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.extras.rxjava2.RxHttpClient;


public class TestNettyClient {
	
	public static void https() {
		
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		CountDownLatch latch = new CountDownLatch(1);
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		
//		// 同步
//		asyncHttpClient.prepareGet("http://www.example.com/").execute();
//		
//		// 异步
//		asyncHttpClient.prepareGet("http://www.example.com/").execute(new AsyncCompletionHandler<Response>() {
//			@Override
//			public Response onCompleted(Response arg0) throws Exception {
//				System.out.println("数据");
//				return arg0;
//			}
//		});
//		
//		// CompletableFuture
//		CompletableFuture<Response> promise = asyncHttpClient
//	            .prepareGet("http://www.example.com/")
//	            .execute()
//	            .toCompletableFuture()
//	            .exceptionally(t ->{ return null;})
//	            .thenApply(resp -> { /*  Do something with the Response */ return resp; });
//	    promise.join(); // wait for completion
	    
	    // reactor rx
	    Request request = new RequestBuilder().setUrl("http://www.example.com/").build();
	    RxHttpClient.create(asyncHttpClient).prepare(request).subscribe((t)->{
	    	System.out.println("rx---" + t);
	    });
	    
		latch.await();
		asyncHttpClient.close();
	}
}