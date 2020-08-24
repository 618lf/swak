package com.swak.vertx.protocol.http;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * 路由处理器
 *
 * @author: lifeng
 * @date: 2020/3/29 20:20
 */
public interface RouterHandler {

	/**
	 * 返回初始化之后的 Router
	 *
	 * @return Router
	 */
	Router getRouter();

	/**
	 * 路由处理器
	 *
	 * @param context 请求上下文
	 * @param handler 处理器
	 */
	void handle(RoutingContext context, MethodInvoker handler);

	/**
	 * 处理错误消息
	 * 
	 * @param e 错误
	 */
	void handle(Throwable e);
}