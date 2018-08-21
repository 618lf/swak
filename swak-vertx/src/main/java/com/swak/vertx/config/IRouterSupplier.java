package com.swak.vertx.config;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * 提供子router
 * @author lifeng
 */
public interface IRouterSupplier {

	/**
	 * Router
	 * @return
	 */
	Router get(Vertx vertx);
	
	/**
	 * 路径
	 * @return
	 */
	String path();
}