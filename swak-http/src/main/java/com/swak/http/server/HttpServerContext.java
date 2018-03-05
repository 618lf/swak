package com.swak.http.server;

import com.swak.http.Executeable;
import com.swak.http.Filter;
import com.swak.http.FilterChain;
import com.swak.http.ServerContext;
import com.swak.http.Servlet;
import com.swak.http.pool.ConfigableThreadPoolExecutor;
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
	private Executeable pool;

	public HttpServerContext(Builder builder) {
		super(builder);
		this.servlet = builder.getServlet();
		this.filter = builder.getFilter();
		this.pool = builder.getPool();
		if (this.pool == null) {
			// 默认使用基于线程的执行器
			this.pool = new ConfigableThreadPoolExecutor();
		}
	}

	public Servlet getServlet() {
		return this.servlet;
	}

	public Filter getFilter() {
		return this.filter;
	}
	
	public Executeable getPool() {
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