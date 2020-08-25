package com.swak.annotation;

/**
 * 提供两种服务器模式
 *
 * @author: lifeng
 * @date: 2020/3/28 17:24
 */
public enum Server {

	/**
	 * 对外提供 Http 服务
	 */
	Http,

	/**
	 * 对外提供 WebSocket 服务
	 */
	Ws,

	/**
	 * 对外提供Tcp 服务 -- 目前暂未支持
	 */
	Tcp
}
