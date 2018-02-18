package com.swak.jwt.exceptions;

public class JWTDecodeException extends JWTVerificationException {
	private static final long serialVersionUID = 1L;

	public JWTDecodeException(String message) {
        this(message, null);
    }

    public JWTDecodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
