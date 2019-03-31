package com.swak.flux.security.exception;

import com.swak.exception.ErrorCode;

/**
 * 用户名密码错误
 * @author lifeng
 */
public class UsernamePasswordException extends AuthenticationException{

	private static final long serialVersionUID = -292585289627016645L;

	public UsernamePasswordException(ErrorCode code) {
		super(code);
	}
}