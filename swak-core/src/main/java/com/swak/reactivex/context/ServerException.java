package com.swak.reactivex.context;

/**
 * 服务器启动异常
 * @author lifeng
 */
public class ServerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServerException(String message, Throwable cause) {
		super(message, cause);
	}
}