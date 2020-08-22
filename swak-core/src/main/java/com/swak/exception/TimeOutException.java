package com.swak.exception;

/**
 * 超时异常
 * 
 * @author lifeng
 * @date 2020年8月22日 下午5:03:35
 */
public class TimeOutException extends BaseRuntimeException {

	private static final long serialVersionUID = 1L;

	public TimeOutException(String msg) {
		super(msg);
	}
}