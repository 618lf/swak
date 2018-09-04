package com.swak.vertx.handler;

import io.vertx.ext.web.RoutingContext;

/**
 * 路由处理器
 * 
 * @author lifeng
 */
public interface RouterHandler {

	/**
	 * 路由处理器
	 * 
	 * @param context
	 * @param handler
	 */
	void handle(RoutingContext context, MethodHandler handler);
}