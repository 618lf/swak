package com.swak.vertx.dsl;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

/**
 * 创建路由
 * 
 * @author lifeng
 */
public class RouterFunctions {

	/**
	 * 根据 Path 创建 Route
	 * 
	 * @param path
	 * @param handler
	 * @return
	 */
	public static RouterFunction Path(String path, Handler<RoutingContext> handler) {
		return (router) -> {
			router.route(path).handler(handler);
		};
	}

	/**
	 * 根据 Path 创建 Route
	 * 
	 * @param path
	 * @param handler
	 * @return
	 */
	public static RouterFunction Get(String path, Handler<RoutingContext> handler) {
		return (router) -> {
			router.route(path).method(HttpMethod.GET).handler(handler);
		};
	}

	/**
	 * 根据 Path 创建 Route
	 * 
	 * @param path
	 * @param handler
	 * @return
	 */
	public static RouterFunction Post(String path, Handler<RoutingContext> handler) {
		return (router) -> {
			router.route(path).method(HttpMethod.POST).handler(handler);
		};
	}
}