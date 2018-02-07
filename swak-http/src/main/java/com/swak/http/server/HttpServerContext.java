package com.swak.http.server;

import com.codahale.metrics.MetricRegistry;
import com.swak.http.Filter;
import com.swak.http.ServerContext;
import com.swak.http.Servlet;
import com.swak.http.server.HttpServer.Builder;

/**
 * http 服务器上下文
 * 
 * @author lifeng
 */
public class HttpServerContext extends ServerContext {

	/*
	 * 简单点，只需要一个 servlet 业务上去做分发即可
	 */
	private Servlet servlet;

	/*
	 * 简单点，只需要一个 filter 过滤所有的请求
	 */
	private Filter filter;

	public HttpServerContext(MetricRegistry registry, Builder builder) {
		super(registry, builder);

		this.servlet = builder.getServlet();
	}

	public Servlet getServlet() {
		return this.servlet;
	}

	public Filter getFilter() {
		return this.filter;
	}
}