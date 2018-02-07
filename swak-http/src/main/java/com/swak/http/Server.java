package com.swak.http;

/**
 * 服务器
 * @author lifeng
 */
public interface Server {

	/**
	 * 启动服务器
	 *
	 * @throws RuntimeException
	 */
	public void start() throws Exception;

	/**
	 * 停止服务器
	 *
	 * @throws RuntimeException
	 */
	public void stop() throws Exception;
}
