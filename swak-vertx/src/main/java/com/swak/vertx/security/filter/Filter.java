package com.swak.vertx.security.filter;

import java.util.concurrent.CompletableFuture;

import io.vertx.ext.web.RoutingContext;

/**
 * 安全过滤器
 * @author lifeng
 */
public interface Filter {

	/**
	 * 执行 filter
	 * @param context
	 * @return
	 */
	CompletableFuture<Boolean> doFilter(RoutingContext context);
}