package com.swak.http;

import javax.net.ssl.SSLException;

import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig.Builder;

import com.swak.closable.ShutDownHook;
import com.swak.http.builder.RequestBuilder;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.ssl.SslContext;

/**
 * Http 服务
 * 
 * @author lifeng
 */
public class HttpService {

	/**
	 * 使用的客户端 -- 线程安全
	 */
	private DefaultAsyncHttpClient client;

	/**
	 * 注册关闭函数
	 */
	public HttpService() {
		ShutDownHook.registerShutdownHook(() -> {
			if (this.client != null) {
				this.client.close();
			}
		});
	}

	/**
	 * 通过配置创建 Http服务
	 * 
	 * @param config
	 * @return
	 */
	public HttpService setConfig(AsyncHttpClientConfig config) {
		Builder builder = new DefaultAsyncHttpClientConfig.Builder(config);
		this.client = new DefaultAsyncHttpClient(builder.build());
		return this;
	}

	/**
	 * 通过配置创建 Http服务
	 * 
	 * @param config
	 * @return
	 * @throws SSLException
	 */
	public HttpService sslConfig(SslContext sslContext) throws RuntimeException {
		AsyncHttpClientConfig config = this.client.getConfig();
		Builder builder = new DefaultAsyncHttpClientConfig.Builder(config).setSslContext(sslContext);
		return new HttpService().setConfig(builder.build());
	}

	/**
	 * 创建一个builder
	 * 
	 * @return
	 */
	public RequestBuilder post() {
		return new RequestBuilder(client).post();
	}

	/**
	 * 创建一个builder
	 * 
	 * @return
	 */
	public RequestBuilder get() {
		return new RequestBuilder(client).get();
	}

	/**
	 * 创建一个builder
	 * 
	 * @return
	 */
	public RequestBuilder method(HttpMethod method) {
		return new RequestBuilder(client).method(method);
	}
}