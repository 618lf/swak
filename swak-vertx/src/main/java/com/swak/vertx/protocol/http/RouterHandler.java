package com.swak.vertx.protocol.http;

import com.swak.vertx.invoker.MethodInvoker;

import io.vertx.ext.web.RoutingContext;

/**
 * 路由处理器
 *
 * @author: lifeng
 * @date: 2020/3/29 20:20
 */
public interface RouterHandler {

	/**
	 * 路由处理器
	 *
	 * @param context 请求上下文
	 * @param handler 处理器
	 */
	void handle(RoutingContext context, MethodInvoker handler);
}