package com.swak.security.exception;

/**
 * 超出了安全控件的生命周期
 * @author lifeng
 */
public class SecurityLifecycleException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public SecurityLifecycleException(String msg) {
		super(msg);
	}
}