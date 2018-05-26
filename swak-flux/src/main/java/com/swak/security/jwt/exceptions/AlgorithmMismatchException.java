package com.swak.security.jwt.exceptions;

public class AlgorithmMismatchException extends JWTVerificationException {
	private static final long serialVersionUID = 1L;

	public AlgorithmMismatchException(String message) {
        super(message);
    }
}
