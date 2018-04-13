package com.swak.common.exception;

/**
 * 访问受限异常
 * @author root
 */
public class AccessDeniedException extends BaseRuntimeException{

	private static final long serialVersionUID = 2553874061546071081L;
	
	public AccessDeniedException(String msg) {
		super(msg);
	}
}