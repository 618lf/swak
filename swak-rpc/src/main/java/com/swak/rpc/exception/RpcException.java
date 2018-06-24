package com.swak.rpc.exception;

/**
 * rpc 标准的异常
 * @author lifeng
 */
public class RpcException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public RpcException(String msg) {
		super(msg);
	}
	
	public RpcException(Exception e) {
		super(e);
	}
}