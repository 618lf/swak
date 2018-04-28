package com.swak.reactivex.server;

import java.util.function.Consumer;

import com.swak.reactivex.server.options.HttpServerOptions;

/**
 * 响应式的 http 服务器
 * @author lifeng
 */
public class ReactiveHttpServer {

	final HttpServerOptions options;
	
	private ReactiveHttpServer(Consumer<? super HttpServerOptions.Builder> options) {
		HttpServerOptions.Builder serverOptionsBuilder = HttpServerOptions.builder();
		options.accept(serverOptionsBuilder);
		this.options = serverOptionsBuilder.build();
	}
	
	/**
	 * 创建 http 服务器
	 * @param options
	 * @return
	 */
	public static ReactiveHttpServer options(Consumer<? super HttpServerOptions.Builder> options) {
		return new ReactiveHttpServer(options);
	}
}