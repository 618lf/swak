package com.swak.http.server;

import com.codahale.metrics.MetricRegistry;
import com.swak.http.Filter;
import com.swak.http.FilterChain;
import com.swak.http.ServerContext;
import com.swak.http.Servlet;
import com.swak.http.pool.ConfigableThreadPool;
import com.swak.http.pool.ConfigableThreadPoolFactory;
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

	/*
	 * 线程池
	 */
	private ConfigableThreadPool pool;

	public HttpServerContext(MetricRegistry registry, Builder builder) {
		super(registry, builder);
		this.servlet = builder.getServlet();
		this.filter = builder.getFilter();
		this.pool = builder.getPool();
		if (this.pool == null) {
			this.pool = new ConfigableThreadPoolFactory();
		}
	}

	public Servlet getServlet() {
		return this.servlet;
	}

	public Filter getFilter() {
		return this.filter;
	}
	
	public ConfigableThreadPool getPool() {
		return pool;
	}

	/**
	 * 构建执行链
	 * 
	 * @return
	 */
	public FilterChain buildFilterChain() {
		return new DefaultFilterChain(this.filter, this.servlet);
	}
}