package com.swak.http;

import com.codahale.metrics.MetricRegistry;
import com.swak.http.server.HttpServer;

/**
 * 服务器的上下文
 * @author lifeng
 */
public abstract class ServerContext {

	private MetricRegistry registry;

	private HttpServer.Builder builder;

	public ServerContext(MetricRegistry registry, HttpServer.Builder builder) {
		this.builder = builder;
		this.registry = registry;
	}

	public MetricRegistry getRegistry() {
		return registry;
	}

	public HttpServer.Builder getBuilder() {
		return builder;
	}
}
