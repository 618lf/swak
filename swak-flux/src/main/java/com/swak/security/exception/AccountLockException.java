package com.swak.security.exception;

import com.swak.exception.ErrorCode;

/**
 * 账户锁定异常
 * @author lifeng
 */
public class AccountLockException extends AuthenticationException {

	private static final long serialVersionUID = 4579680236680135972L;

	public AccountLockException(ErrorCode code) {
		super(code);
	}
}