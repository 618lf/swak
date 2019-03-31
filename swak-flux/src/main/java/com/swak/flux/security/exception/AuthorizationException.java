package com.swak.flux.security.exception;

public class AuthorizationException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public AuthorizationException() {
		super("验证错误");
	}
	public AuthorizationException(String msg) {
		super(msg);
	}
	public AuthorizationException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public AuthorizationException(Throwable cause) {
		super(cause);
	}
}