package com.swak.vertx.handler;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * 提供子router
 * @author lifeng
 */
public interface RouterSupplier {

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