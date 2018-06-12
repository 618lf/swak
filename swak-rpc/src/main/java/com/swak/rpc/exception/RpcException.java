package com.swak.rpc.exception;

import com.swak.exception.BaseRuntimeException;

/**
 * rpc 标准的异常
 * @author lifeng
 */
public class RpcException extends BaseRuntimeException {
	
	private static final long serialVersionUID = 1L;

	public RpcException(String msg) {
		super(msg);
	}
}