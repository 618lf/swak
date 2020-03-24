package com.swak.vertx.annotation;

/**
 * 提供两种服务器模式
 * 
 * @author lifeng
 */
public enum Server {

	/**
	 * 内部 EventBus 调度
	 */
	EventBus,

	/**
	 * 对外提供 Http 服务
	 */
	Http,

	/**
	 * 对外提供Tcp 服务 -- 目前暂未支持
	 */
	Tcp
}
