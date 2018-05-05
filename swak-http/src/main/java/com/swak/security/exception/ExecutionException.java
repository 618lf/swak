package com.swak.security.exception;

public class ExecutionException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ExecutionException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public ExecutionException(Throwable cause) {
		super(cause);
	}
}