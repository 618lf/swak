package com.swak.vertx.config;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.Router;

/**
 * Vertx 一般只有一个
 * @author lifeng
 */
public class VertxFactory {

	/**
	 * 标准的 Vertx 实例
	 */
	public static Vertx V = null;
	
	/**
	 * 标准的 Router 实例
	 */
	public static Router R = null;
	
	VertxFactory() {
		V = Vertx.vertx(new VertxOptions());
		R = Router.router(V);
	}
}