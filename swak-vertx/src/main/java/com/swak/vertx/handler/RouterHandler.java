package com.swak.vertx.handler;

import com.swak.vertx.config.AnnotationBean;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * 路由处理器
 * 
 * @author lifeng
 */
public interface RouterHandler {

	
	/**
	 * 
	 * 根据配置信息 - 初始化router
	 * 
	 * @param annotation
	 * @return
	 */
	void initRouter(Vertx vertx, AnnotationBean annotation);
	
	/**
	 * 返回初始化之后的 Router -- Router 是线程安全的可以在
	 * 
	 * @return
	 */
	Router getRouter();
	
	/**
	 * 做一些初始化的操作
	 * 
	 * @param handler
	 */
	default void initHandler(MethodHandler handler) {}

	/**
	 * 路由处理器
	 * 
	 * @param context
	 * @param handler
	 */
	void handle(RoutingContext context, MethodHandler handler);
}