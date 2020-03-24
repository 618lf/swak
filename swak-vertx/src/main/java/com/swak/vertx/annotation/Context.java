package com.swak.vertx.annotation;

/**
 * 四种上下文模式
 * 
 * @author lifeng
 */
public enum Context {

	/**
	 * 适合异步 IO 的运行模式
	 */
	IO,

	/**
	 * 顺序的执行
	 */
	Order,

	/**
	 * 并发的运行模式
	 */
	Concurrent
}
