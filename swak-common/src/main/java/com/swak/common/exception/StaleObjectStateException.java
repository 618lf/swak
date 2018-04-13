package com.swak.common.exception;

/**
 * 过期的数据
 * @author lifeng
 */
public class StaleObjectStateException extends BaseRuntimeException {

	private static final long serialVersionUID = 6847600967697212891L;

	public StaleObjectStateException(String msg) {
		super(msg);
	}

	public StaleObjectStateException(Throwable cause) {
		super(cause);
	}

	public StaleObjectStateException(String message, Throwable cause) {
		super(message, cause);
	}
}
