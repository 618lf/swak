package com.swak.reactivex.context;

/**
 * 定义一个服务器
 *
 * @author: lifeng
 * @date: 2020/3/29 12:16
 */
public interface Server {

	/**
	 * Starts the web server. Calling this method on an already started server has
	 * no effect.
	 *
	 * @throws ServerException 服务器异常
	 */
	void start() throws ServerException;

	/**
	 * Stops the web server. Calling this method on an already stopped server has no
	 * effect.
	 *
	 * @throws ServerException 务器异常
	 */
	void stop() throws ServerException;

	/**
	 * 可能会启动多个服务
	 *
	 * @return 地址
	 */
	EndPoints getEndPoints();
}
