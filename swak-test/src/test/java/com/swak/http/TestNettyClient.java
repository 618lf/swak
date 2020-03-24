package com.swak.http;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig.Builder;

import com.swak.http.builder.RequestBuilder;
import com.swak.http.resource.SharedNettyCustomizer;
import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.transport.resources.LoopResources;

/**
 * 基于 Netty 的客户端 ChannelManager 启动 bootstrap
 * 
 * @author lifeng
 */
public class TestNettyClient {

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		Builder builder = new DefaultAsyncHttpClientConfig.Builder();

		// 超时时间
		builder.setConnectTimeout(300000);
		builder.setRequestTimeout(300000);
		builder.setReadTimeout(300000);
		builder.setHandshakeTimeout(300000);

		// 自定义 Eventloop
		LoopResources loopResources = Contexts.createEventLoopResources(LoopResources.transportModeFitOs(), 1, -1,
				"AsyncHttp.", false, 2, TimeUnit.SECONDS);
		builder.setEventLoopGroup(loopResources.onClient());
		builder.setThreadPoolName("AsyncHttp.timeout");
		builder.setHttpAdditionalChannelInitializer(new SharedNettyCustomizer());
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient(builder.build());

//		RequestBuilder.get().setUrl("https://www.baidu.com/").text().future()
//				.whenComplete((v, t) -> {
//					System.out.println(v);
//					try {
//						Thread.sleep(5000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				});
		String src = "https://img.yaoyaoliao.com/upload/files/23033/1279527/2020021715818860270.jpg";
		RequestBuilder.client(asyncHttpClient).get().plain().setUrl(src).future().whenComplete((v, e) -> {
			System.out.println(v);
		});
	}
}