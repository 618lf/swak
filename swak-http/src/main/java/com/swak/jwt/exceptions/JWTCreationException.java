package com.swak.jwt.exceptions;

public class JWTCreationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public JWTCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
