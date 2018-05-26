package com.swak.security.jwt.exceptions;

public class JWTVerificationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public JWTVerificationException(String message) {
        this(message, null);
    }

    public JWTVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
