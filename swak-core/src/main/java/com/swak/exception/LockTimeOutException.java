package com.swak.exception;

/**
 * 锁超时异常
 * @author lifeng
 */
public class LockTimeOutException extends BaseRuntimeException{

	private static final long serialVersionUID = 1L;
	
	public LockTimeOutException(String msg) {
		super(msg);
	}
}