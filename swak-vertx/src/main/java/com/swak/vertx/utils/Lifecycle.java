package com.swak.vertx.utils;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * 定义一个生命周期的对象
 * @author lifeng
 */
public class Lifecycle {

	public volatile static Vertx vertx = null;
	public volatile static Router router = null;
	
}