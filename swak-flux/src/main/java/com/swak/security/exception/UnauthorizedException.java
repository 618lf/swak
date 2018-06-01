package com.swak.security.exception;

/**
 * 自定义未授权的错误
 * @author root
 */
public class UnauthorizedException extends AuthorizationException {

	private static final long serialVersionUID = 6112854826339960324L;

	public UnauthorizedException() {
		super();
	}

	public UnauthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnauthorizedException(String message) {
		super(message);
	}

	public UnauthorizedException(Throwable cause) {
		super(cause);
	}
}