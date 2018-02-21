package com.swak.http;

import com.swak.http.server.HttpServer;

/**
 * 服务器的上下文
 * @author lifeng
 */
public abstract class ServerContext {

	private HttpServer.Builder builder;

	public ServerContext(HttpServer.Builder builder) {
		this.builder = builder;
	}
	public HttpServer.Builder getBuilder() {
		return builder;
	}
}